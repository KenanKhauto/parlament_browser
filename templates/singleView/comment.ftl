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
        <h1>${comment.getID()}</h1>
    </div>

    <section>
        <div class="flex container" style="align-items: stretch">
            <div class="card">
                <div class="card-title">
                    <label><i class="fa-regular fa-font" style="margin-right: 15px"></i>Content</label>
                </div>
                <div class="card-body">
                    <label>${comment.getContent()}</label>
                </div>
            </div>

            <div class="card">
                <div class="card-title">
                    <label><i class="fa-solid fa-info" style="margin-right: 15px"></i>Information</label>
                </div>
                <div class="card-body">
                    <#--<label>Author: <a
                                href="/deputy?id=${(comment.getAuthor().getID())!""}">${comment.getAuthor()!""}</a></label><br>-->
                    <label>Speech: <a
                                href="/speech?id=${(comment.getSpeech().getID())!""}">${(comment.getSpeech().getID())!""}</a></label><br>
                </div>
            </div>

        </div>
    </section>

    <#include "../footer.ftl">

</main>

</body>

</html>