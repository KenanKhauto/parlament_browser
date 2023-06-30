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
        <h1><#--${party.getID()} - --> ${party.getName()}</h1>
    </div>

    <section>
        <div class="flex container" style="align-items: stretch">
            <div class="card">
                <div class="card-title">
                    <label><i class="fa-solid fa-info" style="margin-right: 15px"></i>Information</label>
                </div>
                <div class="card-body">
                    <label>Name: ${party.getName()}</label><br>
                    <label>Member Count: ${party.getMembers()?size}</label><br>
                </div>
            </div>

            <div class="card">
                <div class="card-title">
                    <label><i class="fa-solid fa-people-group" style="margin-right: 15px"></i>Members</label>
                </div>
                <div class="card-body">
                    <ul>
                        <#list party.getMembers() as member>
                            <li><a href="/deputy?id=${member.getID()}">${member.getName()}</a></li>
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