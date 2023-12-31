import os
import subprocess
import locale
import os
import re
import glob
import json
import tempfile
import math
import torch
from torch import nn
from torch.nn import functional as F
from torch.utils.data import DataLoader
import numpy as np
import commons
import utils
import argparse
import subprocess
from data_utils import TextAudioLoader, TextAudioCollate, TextAudioSpeakerLoader, TextAudioSpeakerCollate
from models import SynthesizerTrn
from scipy.io.wavfile import write
import soundfile as sf
from flask import Flask, request, send_file
import os.path
from threading import Lock

locale.getpreferredencoding = lambda: "UTF-8"

lock = Lock()

def download(lang, tgt_dir="./"):
  lang_fn, lang_dir = os.path.join(tgt_dir, lang+'.tar.gz'), os.path.join(tgt_dir, lang)
  if os.path.exists(lang_fn):
      return lang_dir
  cmd = ";".join([
        f"wget https://dl.fbaipublicfiles.com/mms/tts/{lang}.tar.gz -O {lang_fn}",
        f"tar zxvf {lang_fn}"
  ])
  print(f"Download model for language: {lang}")
  subprocess.check_output(cmd, shell=True)
  print(f"Model checkpoints in {lang_dir}: {os.listdir(lang_dir)}")
  return lang_dir

# https://dl.fbaipublicfiles.com/mms/tts/all-tts-languages.html
# quy = Quechua, Ayacucho
# qvc = Quechua, Cajamarca
# quz = Quechua, Cusco
# qve = Quechua, Eastern Apurímac
# qub = Quechua, Huallaga
# qvh = Quechua, Huamalíes-Dos de Mayo Huánuco
# qwh = Quechua, Huaylas Ancash
# qvw = Quechua, Huaylla Wanca
# quf = Quechua, Lambayeque
# qvm = Quechua, Margos-Yarowilca-Lauricocha
# qul = Quechua, Norte de Bolivia
# qvn = Quechua, Norte de Junín
# qxn = Quechua, Conchucos Norte, Ancash
# qxh = Quechua, Panao
# qvs = Quechua, San Martín
# quh = Quechua, Sur de Bolivian
# qxo = Quechua, Conchucos, Sur

LANG_LIST =[
    "quy",
    "qvc",
    "quz",
    "qve",
    "qub",
    "qvh",
    "qwh",
    "qvw",
    "quf",
    "qvm",
    "qul",
    "qvn",
    "qxn",
    "qxh",
    "qvs",
    "quh",
    "qxo"
]

def preprocess_char(text, lang=None):
    """
    Special treatement of characters in certain languages
    """
    print(lang)
    if lang == 'ron':
        text = text.replace("ț", "ţ")
    return text

class TextMapper(object):
    def __init__(self, vocab_file):
        self.symbols = [x.replace("\n", "") for x in open(vocab_file, encoding="utf-8").readlines()]
        self.SPACE_ID = self.symbols.index(" ")
        self._symbol_to_id = {s: i for i, s in enumerate(self.symbols)}
        self._id_to_symbol = {i: s for i, s in enumerate(self.symbols)}

    def text_to_sequence(self, text, cleaner_names):
        '''Converts a string of text to a sequence of IDs corresponding to the symbols in the text.
        Args:
        text: string to convert to a sequence
        cleaner_names: names of the cleaner functions to run the text through
        Returns:
        List of integers corresponding to the symbols in the text
        '''
        sequence = []
        clean_text = text.strip()
        for symbol in clean_text:
            symbol_id = self._symbol_to_id[symbol]
            sequence += [symbol_id]
        return sequence

    def uromanize(self, text, uroman_pl):
        iso = "xxx"
        with tempfile.NamedTemporaryFile() as tf, \
             tempfile.NamedTemporaryFile() as tf2:
            with open(tf.name, "w") as f:
                f.write("\n".join([text]))
            cmd = f"perl " + uroman_pl
            cmd += f" -l {iso} "
            cmd +=  f" < {tf.name} > {tf2.name}"
            os.system(cmd)
            outtexts = []
            with open(tf2.name) as f:
                for line in f:
                    line =  re.sub(r"\s+", " ", line).strip()
                    outtexts.append(line)
            outtext = outtexts[0]
        return outtext

    def get_text(self, text, hps):
        text_norm = self.text_to_sequence(text, hps.data.text_cleaners)
        if hps.data.add_blank:
            text_norm = commons.intersperse(text_norm, 0)
        text_norm = torch.LongTensor(text_norm)
        return text_norm

    def filter_oov(self, text):
        val_chars = self._symbol_to_id
        txt_filt = "".join(list(filter(lambda x: x in val_chars, text)))
        print(f"text after filtering OOV: {txt_filt}")
        return txt_filt

def preprocess_text(txt, text_mapper, hps, uroman_dir=None, lang=None):
    txt = preprocess_char(txt, lang=lang)
    is_uroman = hps.data.training_files.split('.')[-1] == 'uroman'
    if is_uroman:
        with tempfile.TemporaryDirectory() as tmp_dir:
            if uroman_dir is None:
                cmd = f"git clone git@github.com:isi-nlp/uroman.git {tmp_dir}"
                print(cmd)
                subprocess.check_output(cmd, shell=True)
                uroman_dir = tmp_dir
            uroman_pl = os.path.join(uroman_dir, "bin", "uroman.pl")
            print(f"uromanize")
            txt = text_mapper.uromanize(txt, uroman_pl)
            print(f"uroman text: {txt}")
    txt = txt.lower()
    txt = text_mapper.filter_oov(txt)
    return txt

def load_lang(lang):
    ckpt_dir = download(lang)
    if torch.cuda.is_available():
        device = torch.device("cuda")
    else:
        device = torch.device("cpu")

    print(f"Run inference with {device}")
    vocab_file = f"{ckpt_dir}/vocab.txt"
    config_file = f"{ckpt_dir}/config.json"
    assert os.path.isfile(config_file), f"{config_file} doesn't exist"
    hps = utils.get_hparams_from_file(config_file)
    text_mapper = TextMapper(vocab_file)
    net_g = SynthesizerTrn(
        len(text_mapper.symbols),
        hps.data.filter_length // 2 + 1,
        hps.train.segment_size // hps.data.hop_length,
        **hps.model)
    net_g.to(device)
    _ = net_g.eval()

    g_pth = f"{ckpt_dir}/G_100000.pth"
    print(f"load {g_pth}")

    _ = utils.load_checkpoint(g_pth, net_g, None)
    return (hps, text_mapper, device, net_g)

def generate_audio(txt, lang, hps, text_mapper, device, net_g):
    print(f"text: {txt}")
    txt = preprocess_text(txt, text_mapper, hps, lang=lang)
    stn_tst = text_mapper.get_text(txt, hps)
    with torch.no_grad():
        x_tst = stn_tst.unsqueeze(0).to(device)
        x_tst_lengths = torch.LongTensor([stn_tst.size(0)]).to(device)
        hyp = net_g.infer(
            x_tst, x_tst_lengths, noise_scale=.667,
            noise_scale_w=0.8, length_scale=1.0
        )[0][0,0].cpu().float().numpy()

    print(f"Generated audio") 
    sf.write('file.ogg', hyp, hps.data.sampling_rate)

app = Flask(__name__)

@app.route('/tts',  methods=['POST'])
def index():
    lang = request.args.get('lang')
    if not lang:
        return "Missing lang parameter", 400
    if lang not in LANG_LIST:
        return "Invalid lang parameter", 400
    print(f'Request received: lang={lang} message={request.data}')
    (hps, text_mapper, device, net_g) = load_lang(lang)
    if not text_mapper and not hps and not device and not net_g:
        return "Invalid language", 400
    with lock:
        generate_audio(request.data.decode(), lang, hps, text_mapper, device, net_g)
        return send_file('file.ogg', mimetype='audio/ogg')
