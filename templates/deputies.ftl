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
        <h1>Deputies</h1>
    </div>

    <section>

        <div class="flex" style="flex-direction: column">
            <div>
                <div class="container">
                    <div style="display: inline-block">
                        <a class="button" style="display: block; width: 10px; margin: auto"
                           href="/deputies?page=<#if page==0>0<#else>${page-1}</#if>">
                            <i class="fa-solid fa-chevron-left"></i>
                        </a>
                        <label style="display: block"><small>Back</small></label>
                    </div>
                    <div style="display: inline-block">
                        <a class="button" style="display: block; width: 10px; margin: auto"
                           href="/deputies?page=<#if page==pageCount>${pageCount}<#else>${page+1}</#if>">
                            <i class="fa-solid fa-chevron-right"></i>
                        </a>
                        <label style="display: block"><small>Next</small></label>
                    </div>
                </div>

                <div class="container flex" style="justify-content: center">
                    <table class="item-list">
                        <thead>
                        <tr class="tr-head">
                            <th>Deputy Name</th>
                            <th>Party</th>
                            <th>Faction</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list speakers as id, deputy>
                            <tr>
                                <td>
                                    <a href="/deputy?id=${deputy.getID()}" style="display: flex; align-items: center">
                                        <img src="${picUrls[id]}"
                                             style="display: block; vertical-align: middle; border-radius: 10px; max-height: 80px; margin-right: 10px">
                                        <label style="font-size: 18px; font-weight: lighter">
                                            <#if deputy.getName() == "">{{ Missing Name }}<#else>${deputy.getName()}</#if>
                                        </label>
                                    </a>
                                </td>
                                <#if deputy.getParty()??>
                                    <td>
                                        <a href="/party?id=${deputy.getParty().getName()!""}">${deputy.getParty().getName()!""}</a>
                                    </td>
                                <#else>
                                    <td><a href="/party?id=''"></a></td>
                                </#if>
                                <#if deputy.getFaction()??>
                                    <td>
                                        <a href="/faction?id=${deputy.getFaction().getName()!""}">${deputy.getFaction().getName()!""}</a>
                                    </td>
                                <#else>
                                    <td><a href="/faction?id=''"></a></td>
                                </#if>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>

                <div class="container" style="padding-top: 10px">
                    <div style="display: inline-block">
                        <a class="button" style="display: block; width: 10px; margin: auto"
                           href="/deputies?page=<#if page==0>0<#else>${page-1}</#if>">
                            <i class="fa-solid fa-chevron-left"></i>
                        </a>
                        <label style="display: block"><small>Back</small></label>
                    </div>
                    <div style="display: inline-block">
                        <a class="button" style="display: block; width: 10px; margin: auto"
                           href="/deputies?page=<#if page==pageCount>${pageCount}<#else>${page+1}</#if>">
                            <i class="fa-solid fa-chevron-right"></i>
                        </a>
                        <label style="display: block"><small>Next</small></label>
                    </div>
                </div>
            </div>
        </div>

    </section>

    <#include "footer.ftl">

</main>

</body>

</html>