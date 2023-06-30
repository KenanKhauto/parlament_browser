<!DOCTYPE html>

<#-- Author: Simon Schuett -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <META NAME="author" CONTENT="Simon Schuett">

    <script>
        $(document).ready(function () {
            $.ajax({
                method: "GET",
                url: "/speechComments?id=${title}",
                contentType: "text/html",
                success: function (data) {
                    $("#speechComments").html(data);
                },
                error: function (error) {
                    $("#speechComments").html(error.responseText);
                }
            });
        })
    </script>

</head>
<body>

<div>

    <div class="flex" style="justify-content: space-between; align-items: flex-start; padding-bottom: 20px">
        <div class="flex" style="align-items: flex-start">
            <#if picUrls[infos["SpeakerID"]] != "">
                <img src="${picUrls[infos["SpeakerID"]]}"
                     style="display: block; vertical-align: middle; border-radius: 10px; max-height: 100px; margin-right: 10px">
            </#if>
            <div>
                <h2 style="margin: 0 0 5px 0">Speech - ${title}</h2>
                ${infos["SpeakerName"]}<br>
                <small>Party: ${infos["Party"]}, </small>
                <small>Faction: ${infos["Faction"]}</small>
            </div>
        </div>
        <div style="text-align: center">
            <label class="annotation"
                   style="background-color: chocolate; display: inline-block; width: 100px; margin-bottom: 4px; height: 19px; line-height: 20px">Person</label>
            <label class="annotation"
                   style="background-color: green; display: block; margin-bottom: 4px; height: 19px; line-height: 20px">Location</label>
            <label class="annotation"
                   style="background-color: darkmagenta; display: block; margin-bottom: 4px; height: 19px; line-height: 20px">Organisation</label>
            <label class="annotation"
                   style="display: block; margin-bottom: 4px; height: 19px; line-height: 20px">Sentiment</label>
        </div>
    </div>

    <div>
        <#list sentences as sentence>

            <#assign newSentence = sentence["text"]>

            <#list persons as person>
                <#if sentence["text"]?contains(person)>
                    <#assign newSentence = sentence["text"]?replace(person, "<label class='annotation' style='background-color: chocolate'>" + person + "</label>")>
                </#if>
            </#list>

            <#list locations as location>
                <#if sentence["text"]?contains(location)>
                    <#assign newSentence = sentence["text"]?replace(location, "<label class='annotation' style='background-color: green'>" + location + "</label>")>
                </#if>
            </#list>

            <#list organisations as organisation>
                <#if sentence["text"]?contains(organisation)>
                    <#assign newSentence = sentence["text"]?replace(organisation, "<label class='annotation' style='background-color: darkmagenta'>" + organisation + "</label>")>
                </#if>
            </#list>

            ${newSentence}

            <span>
                 <label class="annotation" style="margin-right: 5px">${sentence["sentiment"]}</label>
            </span>
        </#list>

    </div>

    <div>
        <h3 style="margin-bottom: 5px">Comments</h3>
        <div id="speechComments">
            <i id="rl-spinner" class="fa-solid fa-rotate fa-spin"></i>
        </div>
    </div>

</div>


</body>
</html>