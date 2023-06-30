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
        <h1>Speech ${speech.getID()}</h1>
    </div>

    <section>
        <div class="flex container" style="align-items: stretch">
            <div class="card">
                <div class="card-title">
                    <label><i class="fa-regular fa-font" style="margin-right: 15px"></i>Content</label>
                </div>
                <div class="card-body">
                    <label>${speech.getText()}</label>
                </div>
            </div>

            <div class="card">
                <div class="card-title">
                    <label><i class="fa-solid fa-info" style="margin-right: 15px"></i>Information</label>
                </div>
                <div class="card-body">
                    <label>Author:
                        <a href="/deputy?id=${(speech.getSpeaker().getID())!""}">${speech.getSpeaker().getName()!""}</a>
                    </label><br>
                    <#--<label>
                        Agenda Item:
                        <a href="/agendaItem?id=${(speech.getDayTopic().getID())!""}">${(speech.getDayTopic().getID())!""}</a>
                    </label><br>-->
                </div>
            </div>

            <div class="card">
                <div class="card-title">
                    <label><i class="fa-regular fa-comment" style="margin-right: 15px"></i>Comments</label>
                </div>
                <div class="card-body" style="max-width: 600px">
                    <ul>
                        <#list speech.getComments() as comment>
                            <li>
                                <a href="/comment?id=${comment.getID()}"><b>${comment.getID()}</b></a>:${comment.getContent()}
                            </li>
                        </#list>
                    </ul>
                </div>
            </div>

        </div>
    </section>

    <#include "../footer.ftl">

</main>

</body>

</html>