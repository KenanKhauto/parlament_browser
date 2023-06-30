<!DOCTYPE html>

<#-- Author: Simon Schuett -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${title}</title>

    <META NAME="author" CONTENT="Simon Schuett">

    <style>
        <#include "public/css/style.css">
    </style>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css"/>

</head>
<body>

<#include "topnav.ftl">
<#include "sidenav.ftl">

<main>

    <div class="container">
        <h1>Parties</h1>
    </div>

    <section>
        <div class="container flex" style="justify-content: center">
            <table class="item-list">
                <thead>
                <tr class="tr-head">
                    <th>Party Name</th>
                    <th>Members</th>
                </tr>
                </thead>
                <tbody>
                <#list parliament.getParties() as id, party>
                    <tr>
                        <td><a href="/party?id=${party.getName()}">${party.getName()}</a></td>
                        <td>
                            <div style="overflow: scroll; max-height: 200px">
                                <#list party.getMembers() as member>
                                    <a href="/deputy?id=${member.getID()}">${member.getName()}</a>
                                </#list>
                            </div>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </section>

    <#include "footer.ftl">

</main>

</body>

</html>