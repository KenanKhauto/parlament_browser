<!DOCTYPE html>

<#-- Author: Simon Schuett -->

<html lang="en">
<head>
    <meta charset="UTF-8">
</head>
<body>

<div>
    <ul style="margin: 0; padding-left: 20px">
        <#list speech.getComments() as comment>
            <li>
                <#--<b>${comment.getIndex()}</b>:-->${comment.getContent()}
            </li>
        </#list>
    </ul>
</div>

</body>

</html>