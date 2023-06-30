<!DOCTYPE html>

<#-- Author: Simon Schuett -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${title}</title>

    <META NAME="author" CONTENT="Simon Schuett">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>

    <style>
        <#include "public/css/style.css">
    </style>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css"/>

    <script>

        $(document).ready(function () {

            $(".protocol-elem").on("click", function () {
                // $(this).toggleClass("active");
                $("#PDFView").html("<i id='rl-spinner' class='fa-solid fa-rotate fa-spin'></i>");

                let id = $(this).attr("id");

                $.ajax({
                    method: "GET",
                    url: "/protocolPdf?protocol=" + id,
                    contentType: "text/html",
                    success: function (data) {
                        $("#PDFView").html(data);
                    },
                    error: function (error) {
                        $("#PDFView").html(error.responseText);
                    }
                });
            })

            $.ajax({
                method: "GET",
                url: "/protocolPdf?protocol=1-19",
                contentType: "text/html",
                success: function (data) {
                    $("#PDFView").html(data);
                },
                error: function (error) {
                    $("#PDFView").html(error.responseText);
                }
            });

        })

    </script>

</head>
<body>

<#include "topnav.ftl">
<#include "sidenav.ftl">

<main>

    <div class="container">
        <h1>PDF Export</h1>
    </div>

    <section style="padding: 0; background-color: #282828">

        <div style="background-image: url('notbundestagbutowncopyright_blur.jpeg'); background-size: cover;
        box-shadow: 0 60px 32px -20px inset #282828">

            <div class="container" style="display: flex; justify-content: space-between; gap: 20px; align-items: flex-start;
                padding-top: 100px; padding-bottom: 20px">

                <div style="background-color: #282828; padding: 20px; border-radius: 15px; flex-basis: 250px; flex-grow: 1">

                    <ul id="protocol-list" class="collapse-list">
                        <#list parliament.getProtocolIDs() as protocol>
                            <li>
                                <span class="fold-element protocol-elem" id="${protocol}"
                                      style="padding-left: 10px">
                                    <i class="fa-regular fa-file-lines"></i>
                                    Protocol ${protocol}
                                </span>
                            </li>
                        </#list>
                    </ul>

                </div>

                <div id="PDFView"
                     style="background-color: #282828; padding: 20px; border-radius: 15px; flex-shrink: 4; flex-grow: 8">
                    <i id="rl-spinner" class="fa-solid fa-rotate fa-spin"></i>
                </div>

            </div>
        </div>
    </section>

    <#include "footer.ftl">

</main>

</body>
</html>