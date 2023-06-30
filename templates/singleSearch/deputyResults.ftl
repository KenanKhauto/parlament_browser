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
        <h1>Deputies</h1>
    </div>

    <section>
        <div class="panel flex" style="justify-content: center" id="result">
            <table class="item-list">
                <thead>
                <tr class="tr-head">
                    <th>Deputy Name</th>
                    <th>Party</th>
                    <th>Faction</th>
                </tr>
                </thead>
                <tbody>
                <#list deputies as deputy>
                    <tr>
                        <td><a href="/deputy?id=${deputy.getID()}">${deputy.getFirstName()} ${deputy.getLastName()} </a>
                        </td>
                        <td><a href="/party?id=${deputy.getParty()!""}">${deputy.getParty()!""}</a></td>
                        <td><a href="/faction?id=${deputy.getFaction()!""}">${deputy.getFaction()!""}</a></td>
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