<#-- @ftlvariable name="uiModel" type="com.cramsan.runasimi.service.controller.HtmlUIModel" -->
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
    <span>Mensaje</span><input type="text" id="message" name="message" maxlength="50" value="${uiModel.initialQuery}">
    <input type="submit" value="Generar">
</form>

<p>Algunas frases que puedes probar:</p>

Kunanchu ripunki? - <i>¿Ahora te vas?</i><br>
Imaynallam kachkanki? - <i>¿Cómo estás?</i><br>
¿Haykaqtaq ripunki? - <i>¿Cuándo te vas?</i><br>
Haykatataq munanki? - <i>¿Cuánto quieres?</i><br>
Hayka watayuqtaq kanki? - <i>¿Cuántos años tienes?</i><br>
Imataq sutiyki? - <i>¿Cuál es tu nombre?</i><br>
Maymantataq kanki? - <i>¿De dónde eres?</i><br>
Maymantataq hamunki? - <i>¿De dónde vienes?</i><br>
Allinllam kachkani - <i>Estoy bien</i><br>
Tutakama/Ampikama. - <i>Hasta la noche</i><br>
Paqarinkama/Waraykama. - <i>Hasta mañana</i><br>
Paqarin tuta/Waray ampi. - <i>Mañana por la noche</i><br>
Unquchkankichu?/Keshyankiku? - <i>¿Estas enfermo?</i><br>
Imatataq ruwachkanki? - <i>¿Qué estás haciendo?</i><br>
Imatataq munanki? - <i>¿Qué quieres?</i><br>
Imaykitaq nanan? - <i>¿Qué te duele?</i><br>
Imatataq apamunki? - <i>¡Que traes?</i><br>
Imatataq rantikunki? - <i>¿Qué vendes?</i><br>
Munankichu mikuyta? - <i>¿Quieres comer?</i><br>
Quñisunkichu, chirisunkichu? - <i>¿Sientes calor?</i><br>

</body>
</html>