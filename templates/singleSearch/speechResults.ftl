<!DOCTYPE html>

<#-- Author: Simon Schuett -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${title}</title>

    <META NAME="author" CONTENT="Simon Schuett">

    <style>
        <#include "../public/css/style.css">
    </style>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css"/>

</head>
<body>

<#include "../topnav.ftl">
<#include "../sidenav.ftl">

<main>

    <div class="container">
        <h1>Speech Search</h1>
    </div>

    <section>
        <div class="container" id="result">
            <table class="item-list">
                <thead>
                <tr class="tr-head">
                    <th>Speech ID</th>
                    <th>Speaker</th>
                    <th>Content</th>
                </tr>
                </thead>
                <tbody>
                <#list speeches as speech>
                    <tr>
                        <td><a href="/speech?id=${speech.getID()}">${speech.getID()}</a></td>
                        <td>
                            <a href="/deputy?id=${(speech.getSpeaker().getID())!""}">${(speech.getSpeaker().getName())!""}</a>
                        </td>
                        <td>
                            <div style="overflow: scroll; max-height: 200px">
                                ${speech.getText()}
                            </div>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </section>

    <#include "../footer.ftl">

</main>

</body>
</html>