git clone https://github.com/jaywalnut310/vits.git
cd vits/
cd monotonic_align/
mkdir monotonic_align
python3 setup.py build_ext --inplace
cd ../
mv ../server.py .
flask --app server run --host=0.0.0.0
