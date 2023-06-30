<!DOCTYPE html>

<#-- Author: Simon Schuett -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <META NAME="author" CONTENT="Simon Schuett">
</head>
<body>

<#list protocol.getDayTopicList() as dayTopic>
    <li>
        <span class="fold-element topic-elem" id="${dayTopic.getID()}" style="padding: 0">
            <small><i class="fa-solid fa-chevron-right"></i>${dayTopic.getName()}</small>
        </span>
        <ul style="padding-left: 10px">

        </ul>
    </li>
</#list>

</body>
</html>