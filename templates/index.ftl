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

    <#--<link rel="shortcut icon" href="favicon.png" type="image/png">
    <link rel="icon" href="favicon.png" type="image/png">-->

    <#--<link rel="apple-touch-icon" href="/favicon.png">-->
    <link rel="icon" href="/favicon.png">

    <script>

        let topSpeakersLoaded = false;
        let commentCountLoaded = false;
        let speechCountLoaded = false;

        let reloadStats = function () {
            $("#rl-stats").addClass("fa-spin");

            $.ajax({
                method: "GET",
                url: "/stats/top-speakers",
                contentType: "application/json",
                success: function (data) {
                    let docs = JSON.parse(data);
                    console.log(docs);

                    $("#top-speaker1 a").html(docs[0]._id);
                    $("#top-speaker2 a").html(docs[1]._id);
                    $("#top-speaker3 a").html(docs[2]._id);

                    $("#top-speaker1 a").attr("href", "/deputy?id=" + docs[0]._id);
                    $("#top-speaker2 a").attr("href", "/deputy?id=" + docs[1]._id);
                    $("#top-speaker3 a").attr("href", "/deputy?id=" + docs[2]._id);

                    $("#top-spk1-count").html(docs[0].speechCount);
                    $("#top-spk2-count").html(docs[1].speechCount);
                    $("#top-spk3-count").html(docs[2].speechCount);

                    topSpeakersLoaded = true;
                    allLoaded();
                },
                error: function (error) {
                    $("#top-speaker1").html(error.responseText);
                }
            });

            $.ajax({
                method: "GET",
                url: "/stats/speechCount",
                contentType: "text/plain",
                success: function (data) {
                    console.log(data);
                    $("#speechCount").html(data);

                    speechCountLoaded = true;
                    allLoaded();
                },
                error: function (error) {
                    $("#speechCount").html(error.responseText);
                }
            });

            $.ajax({
                method: "GET",
                url: "/stats/commentCount",
                contentType: "text/plain",
                success: function (data) {
                    console.log(data);
                    $("#commentCount").html(data);

                    commentCountLoaded = true;
                    allLoaded();
                },
                error: function (error) {
                    $("#commentCount").html(error.responseText);
                }
            });

        }

        function allLoaded() {
            if (speechCountLoaded && commentCountLoaded && topSpeakersLoaded) {
                $("#rl-stats").removeClass("fa-spin");
            }
        }

        $(document).ready(function () {
            $("#btn-stats").click(reloadStats);

            reloadStats();
        });

    </script>

</head>
<body>

<#include "topnav.ftl">
<#include "sidenav.ftl">

<main>
    <div class="container">
        <h1>Parliament Browser</h1>
    </div>

    <section style="padding: 0; background-color: #282828">

        <div id="analysis-container" style="background-image: url('notbundestagbutowncopyright_blur.jpeg'); background-size: cover;
        box-shadow: 0 60px 32px -20px inset #282828; height: 1000px">

            <div class="flex container" style="padding-top: 100px">

                <div class="card" style="min-width: 500px">
                    <div class="card-title">
                        <label><i class="fa-solid fa-chart-line" style="margin-right: 15px"></i>Statistics</label>
                        <button id="btn-stats">
                            <i id="rl-stats" class="fa-solid fa-rotate">
                            </i>
                        </button>
                    </div>
                    <div class="card-body">

                        <div style="background-color: #222222; border-radius: 10px; padding: 15px; margin-bottom: 15px">
                            <label style="font-weight: lighter">TOP 3 Speakers</label>
                            <div style="padding-top: 12px">
                                <div id="top-speaker1" class="flex" style="justify-content: space-between">
                                    <div class="flex">
                                        <div style="width: 20px; display: flex; justify-content: center">
                                            <i class="fa-solid fa-1" style="margin-right: 5px"></i>
                                        </div>
                                        <i class="fa-solid fa-award" style="margin-right: 10px; color: gold"></i>
                                        <a href=""></a>
                                    </div>
                                    <label><label id="top-spk1-count"></label> Speeches</label>
                                </div>
                                <div id="top-speaker2" class="flex" style="justify-content: space-between">
                                    <div class="flex">
                                        <div style="width: 20px; display: flex; justify-content: center">
                                            <i class="fa-solid fa-2" style="margin-right: 5px"></i>
                                        </div>
                                        <i class="fa-solid fa-award" style="margin-right: 10px; color: silver"></i>
                                        <a href="">
                                        </a>
                                    </div>
                                    <label><label id="top-spk2-count"></label> Speeches</label>
                                </div>
                                <div id="top-speaker3" class="flex" style="justify-content: space-between">
                                    <div class="flex">
                                        <div style="width: 20px; display: flex; justify-content: center">
                                            <i class="fa-solid fa-3" style="margin-right: 5px"></i>
                                        </div>
                                        <i class="fa-solid fa-award" style="margin-right: 10px; color: saddlebrown"></i>
                                        <a href=""></a>
                                    </div>
                                    <label><label id="top-spk3-count"></label> Speeches</label>
                                </div>
                            </div>
                        </div>

                        <div class="flex" style="justify-content: space-between; gap:15px">
                            <div style="background-color: #222222; border-radius: 10px; padding: 15px; flex-grow: 1"
                                 class="flex">
                                <i class="fa-regular fa-comments fa-2xl" style="margin-right: 20px"></i>
                                <div>
                                    <label>Speeches</label>
                                    <div>
                                        <label id="speechCount" style="font-size: 20px"></label>
                                    </div>
                                </div>
                            </div>

                            <div style="background-color: #222222; border-radius: 10px; padding: 15px; flex-grow: 1"
                                 class="flex">
                                <i class="fa-regular fa-comment fa-2xl" style="margin-right: 20px"></i>
                                <div>
                                    <label>Comments</label>
                                    <div>
                                        <label id="commentCount" style="font-size: 20px"></label>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>

                </div>
            </div>
        </div>
    </section>

    <#include "footer.ftl">

</main>

</body>

</html>