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
        <h1>Comment Search</h1>
    </div>

    <section>
        <div class="container flex" style="justify-content: center" id="result">
            <table class="item-list">
                <thead>
                <tr class="tr-head">
                    <th>Comment ID</th>
                    <th>Author</th>
                    <th>Content</th>
                </tr>
                </thead>
                <tbody>
                <#list comments as comment>
                    <tr>
                        <td style="white-space: nowrap">
                            <a href="#<#--/comment?id=${comment.getID()}-->">${comment.getID()}</a>
                        </td>
                        <td>
                            <a href="/deputy?id=${(comment.getAuthor().getID())!""}">${(comment.getAuthor().getFirstname())!""}</a>
                        </td>
                        <td>${comment.getContent()}</td>
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