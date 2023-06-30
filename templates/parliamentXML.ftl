<!DOCTYPE html>
<#-- Author: Kenan Khauto -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${title}</title>

    <style>
        <#include "public/css/style.css">
    </style>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css"/>

    <#include "topnav.ftl">

    <#include "sidenav.ftl">
</head>
<body>
</body>

<main>

    <section>
        <div class="container">
            <h1>${title}</h1>
        </div>
        <div style="padding-left: 30px; padding-top: 20px">
            <button class="login-btn" type="button" value="getPartys()"
                    onclick="location.href='/load-parliament/getPartysXML'">getPartys()
            </button>
            <br>
            <br>
            <button class="login-btn" type="button" value="getFactions()"
                    onclick="location.href='/load-parliament/getFactionsXML'">getFactions()
            </button>
            <br>
            <br>
            <button class="login-btn" type="button" value="getSpeakers()"
                    onclick="location.href='/load-parliament/getSpeakersXML'">getSpeakers()
            </button>
            <br>
            <br>
            <button class="login-btn" type="button" value="getProtocols()"
                    onclick="location.href='/load-parliament/getProtocolsXML'">getProtocols()
            </button>
            <br>
            <br>
        </div>
    </section>

</main>


<#include "footer.ftl">
</html>