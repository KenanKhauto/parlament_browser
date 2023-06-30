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
        <h1>${deputy.getID()!"{{ Missing ID }}"} - ${deputy.getName()}</h1>
    </div>

    <section>
        <div class="flex container" style="align-items: stretch">
            <div class="card">
                <div class="card-title">
                    <label><i class="fa-solid fa-info" style="margin-right: 15px"></i>Personal Information</label>
                </div>
                <div class="card-body">
                    <div class="flex" style="align-items: flex-start">
                        <img src="${picUrls[deputy.getID()]}"
                             style="display: block; vertical-align: middle; border-radius: 10px; max-height: 250px">
                        <div style="padding: 10px">
                            <label>Name: ${deputy.getName()}</label><br>
                            <label>Party:
                                <a href="/party?id=${deputy.getParty().getName()}">${deputy.getParty().getName()}</a>
                            </label><br>
                            <#--<label>In Government: ${deputy.isGovernment()?c}</label><br>
                            <label>Role: ${deputy.getRole()}</label><br>-->
                            <#if speakerInfos["birthdate"] != "">
                                <label>Birth Date: ${speakerInfos["birthdate"]}</label><br>
                            </#if>
                            <#if speakerInfos["birthplace"] != "">
                                <label>Birth Place: ${speakerInfos["birthplace"]}</label><br>
                            </#if>
                            <#if speakerInfos["birthland"] != "">
                                <label>Birth Land: ${speakerInfos["birthland"]}</label><br>
                            </#if>
                            <#if speakerInfos["deathdate"] != "">
                                <label>Death Date: ${speakerInfos["deathdate"]}</label><br>
                            </#if>
                            <#if speakerInfos["sex"] != "">
                                <label>Sex: ${speakerInfos["sex"]}</label><br>
                            </#if>
                            <#if speakerInfos["familystatus"] != "">
                                <label>Family Status: ${speakerInfos["familystatus"]}</label><br>
                            </#if>
                            <#if speakerInfos["profession"] != "">
                                <label>Profession: ${speakerInfos["profession"]}</label><br>
                            </#if>
                            <#-- <label>Faction: <a href="/faction?id=${deputy.getFaction().getName()}">${deputy.getFaction().getName()}</a></label><br>-->
                        </div>
                    </div>
                </div>
            </div>

            <#if speakerInfos["vita"] != "">
                <div class="card">
                    <div class="card-title">
                        <label><i class="fa-solid fa-list-check" style="margin-right: 15px"></i>Vita</label>
                    </div>
                    <div class="card-body" style="max-width: 600px">
                        <label>${speakerInfos["vita"]}</label>
                    </div>
                </div>
            </#if>

            <div class="card">
                <div class="card-title">
                    <label><i class="fa-solid fa-comments" style="margin-right: 15px"></i>Speeches</label>
                </div>
                <div class="card-body">
                    <ul>
                        <#list speakerSpeeches as speech>
                            <li><a href="/speech?id=${speech}">${speech}</a></li>
                        </#list>
                        <#list deputy.getSpeeches() as speech>
                            <li><a href="/speech?id=${speech.getID()}">${speech.getID()}</a></li>
                        </#list>
                    </ul>
                </div>
            </div>

            <#--<div class="card">
                <div class="card-title">
                    <label><i class="fa-solid fa-comment" style="margin-right: 15px"></i>Comments</label>
                </div>
                <div class="card-body">
                    <ul>
                        <#list deputy.getComments() as comment>
                            <li><a href="/comment?id=${comment.getID()}">${comment.getID()}</a></li>
                        </#list>
                    </ul>
                </div>
            </div>-->
        </div>
    </section>

    <#include "../footer.ftl">

</main>

</body>

</html>