<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Google tag (gtag.js) -->
<script async src="https://www.googletagmanager.com/gtag/js?id=G-M4374C90QT"></script>
<script>
    window.dataLayer = window.dataLayer || [];
    function gtag(){dataLayer.push(arguments);}
    gtag('js', new Date());

    gtag('config', 'G-M4374C90QT');
</script>
</head>
<body>
<h1>Runasimi TTS</h1>

<p>Esta página es una demostración de un servicio TTS(text-to-speech)  para el lenguaje quechua.</p>
<p>
    Instrucciones:
    <ul>
        <li>Pon un texto en quechua para general el archivo de voz.</li>
        <li>Dale al botón de <b>Generar</b> para iniciar el proceso.</li>
    </ul>
</p>

<p>
    Características:
    <ul>
        <li>El servicio tiene recursos limitados, puede ser que tome hasta 30 segundos en generar un archivo.</li>
        <li>El mensaje tiene un límite de 50 letras.</li>
        <li>El resultado será un archivo .ogg que se descargara en tu dispositivo.</li>
        <li>Actualmente solo el lenguaje Quechua/Ayacucho</li>
        <li>Este servicio usa el sistema MMST desarrollado por <a href="https://about.fb.com/news/2023/05/ai-massively-multilingual-speech-technology/">Meta</a>.</li>
    </ul>
</p>

<p>Para preguntas o sugerencias: <b>contact@cramsan.com</b></p>

<form action="/tts-form" enctype="multipart/form-data" method="post">
    <span>Mensaje</span><input type="text" id="message" name="message" maxlength="50">
    <input type="submit" value="Generar">
</form>

</body>
</html>