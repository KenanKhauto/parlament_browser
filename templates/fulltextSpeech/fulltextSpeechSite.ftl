<!DOCTYPE html>

<#-- Author: Simon Schuett -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${title}</title>

    <META NAME="author" CONTENT="Simon Schuett">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <script src="https://d3js.org/d3.v7.min.js"></script>

    <style>
        <#include "../public/css/style.css">
    </style>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css"/>

    <script>
        <#include "../javascript/d3charts.js">
    </script>

    <script>

        $(document).ready(function () {

            $(".protocol-elem").on("click", function () {
                $(this).toggleClass("active");
                $(this).next("ul").toggleClass("active");

                let id = $(this).attr("id");

                $.ajax({
                    method: "GET",
                    url: "/dayTopicList?protocol=" + $(this).attr("id"),
                    contentType: "text/html",
                    success: function (data) {
                        $("#" + id).next("ul").html(data);
                    },
                    error: function (error) {
                        $("#" + id).next("ul").html(error.responseText);
                    }
                });
            })

            $(".dayTopic-list").on("click", "span.topic-elem", function () {
                $(this).toggleClass("active");
                $(this).next("ul").toggleClass("active");

                let id = $(this).attr("id");
                console.log(id)

                $.ajax({
                    method: "GET",
                    url: "/speechList?dayTopic=" + $(this).attr("id"),
                    contentType: "text/html",
                    success: function (data) {
                        let speeches = JSON.parse(data);
                        console.log(speeches)
                        speeches.forEach(speech => {
                            $("#" + id).next("ul").append(`<li><span class="speechElem" id="`
                                + speech.id + `" style="padding: 0 5px"><small><i class="fa-solid fa-comments"></i>`
                                + speech.id + ` by ` + speech.speaker + `</small></span></li>`);
                        });
                    },
                    error: function (error) {
                        $("#" + id).next("ul").html(error.responseText);
                    }
                });
            })

            $(".dayTopic-list").on("click", "span.speechElem", function () {
                $.ajax({
                    method: "GET",
                    url: "/fulltextSpeech?speech=" + $(this).attr("id"),
                    contentType: "text/html",
                    success: function (data) {
                        $("#SpeechAnalysis").html(data);
                    },
                    error: function (error) {
                        $("#SpeechAnalysis").html(error.responseText);
                    }
                });
            })

            $.ajax({
                method: "GET",
                url: "/fulltextSpeech?speech=ID19100300",
                contentType: "text/html",
                success: function (data) {
                    $("#SpeechAnalysis").html(data);
                },
                error: function (error) {
                    $("#SpeechAnalysis").html(error.responseText);
                }
            });

        })

    </script>

</head>
<body>

<#include "../topnav.ftl">
<#include "../sidenav.ftl">

<main>

    <div class="container">
        <h1>Fulltext Speech Analysis</h1>
    </div>

    <section style="padding: 0; background-color: #282828">

        <div style="background-image: url('notbundestagbutowncopyright_blur.jpeg'); background-size: cover;
        box-shadow: 0 60px 32px -20px inset #282828">


            <div class="container" style="display: flex; justify-content: space-between; gap: 20px; align-items: flex-start;
            /*backdrop-filter: blur(32px); -webkit-backdrop-filter: blur(32px);*/ padding-top: 100px; padding-bottom: 20px">

                <div style="background-color: #282828; padding: 20px; border-radius: 15px; flex-basis: 250px; flex-grow: 1">

                    <ul id="protocol-list" class="collapse-list">
                        <#list parliament.getProtocolIDs() as protocol>
                            <li>
                                <span class="fold-element protocol-elem" id="${protocol <#--protocol.getID()-->}">
                                    <i class="fa-solid fa-chevron-right"></i>
                                    Protocol ${protocol <#--protocol.getTitle()-->}
                                </span>
                                <ul class="dayTopic-list">

                                </ul>
                            </li>
                        </#list>
                    </ul>

                    <#--<ul id="protocol-list2" class="collapse-list">
                        <li id="prot-1">
                        <span class="fold-element">
                            <i class="fa-solid fa-chevron-right"></i>1. Protocol
                        </span>
                            <ul>
                                <li>
                                <span class="fold-element">
                                    <i class="fa-solid fa-chevron-right"></i>1. Agenda Item
                                </span>
                                    <ul>
                                        <li><span class="speechElem" id="ID19800100">Speech 1</span></li>
                                        <li><span class="speechElem">Speech 2</span></li>
                                        <li><span class="speechElem">Speech 3</span></li>
                                    </ul>
                                </li>
                                <li>
                                <span class="fold-element">
                                    <i class="fa-solid fa-chevron-right"></i>2. Agenda Item
                                </span>
                                    <ul>
                                        <li><span class="speechElem">Speech 1</span></li>
                                        <li><span class="speechElem">Speech 2</span></li>
                                        <li><span class="speechElem">Speech 3</span></li>
                                    </ul>
                                </li>
                            </ul>
                        </li>
                        <li id="prot-2">
                        <span class="fold-element">
                            <i class="fa-solid fa-chevron-right"></i>2. Protocol
                        </span>
                            <ul>
                                <li>
                                <span class="fold-element">
                                    <i class="fa-solid fa-chevron-right"></i>1. Agenda Item
                                </span>
                                    <ul>
                                        <li><span class="speechElem">Speech 1</span></li>
                                        <li><span class="speechElem">Speech 2</span></li>
                                        <li><span class="speechElem">Speech 3</span></li>
                                    </ul>
                                </li>
                                <li>
                                <span class="fold-element">
                                    <i class="fa-solid fa-chevron-right"></i>2. Agenda Item
                                </span>
                                    <ul>
                                        <li><span class="speechElem">Speech 1</span></li>
                                        <li><span class="speechElem">Speech 2</span></li>
                                        <li><span class="speechElem">Speech 3</span></li>
                                    </ul>
                                </li>
                            </ul>
                        </li>
                    </ul>-->

                </div>

                <div id="SpeechAnalysis"
                     style="background-color: #282828; padding: 20px; border-radius: 15px; flex-shrink: 4; flex-grow: 8">
                    <i id="rl-spinner" class="fa-solid fa-rotate fa-spin"></i>
                </div>

            </div>
        </div>
    </section>

    <#include "../footer.ftl">

</main>

</body>
</html>