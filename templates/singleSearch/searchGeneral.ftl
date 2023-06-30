<!DOCTYPE html>

<#-- Author: Simon Schuett -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Search Results for "${term}"</title>

    <META NAME="author" CONTENT="Simon Schuett">

    <style>
        <#include "../public/css/style.css">
    </style>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css"/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>

    <script>

        $(document).ready(function () {
            $("#load-comments").addClass("fa-spin");
            $("#load-speeches").addClass("fa-spin");
            $("#load-deputies").addClass("fa-spin");

            $.ajax({
                method: "GET",
                url: "/commentResults?term=${term}",
                contentType: "text/html",
                success: function (data) {
                    $("#commentSearch").html($(data).find("#result").html());
                    $("#load-comments").removeClass("fa-spin");
                },
                error: function (error) {
                    $("#commentSearch").html(error.responseText);
                }
            });

            $.ajax({
                method: "GET",
                url: "/speechResults?term=${term}",
                contentType: "text/html",
                success: function (data) {
                    $("#speechSearch").html($(data).find("#result").html());
                    $("#load-speeches").removeClass("fa-spin");
                },
                error: function (error) {
                    $("#speechSearch").html(error.responseText);
                }
            });

            $.ajax({
                method: "GET",
                url: "/deputyResults?lastname=${term}",
                contentType: "text/html",
                success: function (data) {
                    $("#separator").before($(data).find("#result").html());
                    $("#load-deputies").removeClass("fa-spin");
                },
                error: function (error) {
                    $("#deputySearch").html(error.responseText);
                }
            });

            $.ajax({
                method: "GET",
                url: "/deputyResults?firstname=${term}",
                contentType: "text/html",
                success: function (data) {
                    $("#separator").after($(data).find("#result").html());
                    $("#load-deputies").removeClass("fa-spin");
                },
                error: function (error) {
                    $("#deputySearch").html(error.responseText);
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
        <h1>Search Results for "${term}"</h1>
    </div>

    <section>
        <div class="flex container">

            <div class="card" style="min-width: 500px">
                <div class="card-title">
                    <label><i class="fa-regular fa-comments" style="margin-right: 15px"></i>Speeches</label>
                    <button id="load-speeches"><i id="rl-entities" class="fa-solid fa-rotate"></i></button>
                </div>
                <div class="card-body" id="speechSearch" style="max-height: 500px">

                </div>
            </div>

            <div class="card" style="min-width: 500px">
                <div class="card-title">
                    <label><i class="fa-regular fa-comment" style="margin-right: 15px"></i>Comments</label>
                    <button id="load-comments"><i id="rl-entities" class="fa-solid fa-rotate"></i></button>
                </div>
                <div class="card-body" id="commentSearch" style="max-height: 500px">

                </div>
            </div>

            <div class="card" style="min-width: 100px">
                <div class="card-title">
                    <label><i class="fa-solid fa-user-tie" style="margin-right: 15px"></i>Deputies</label>
                    <button id="load-deputies"><i id="rl-entities" class="fa-solid fa-rotate"></i></button>
                </div>
                <div class="card-body" id="deputySearch" style="max-height: 500px">

                    <hr id="separator"
                        style="border-bottom: 1px solid whitesmoke; border-top: 1px solid whitesmoke; height: 4px; margin-top: 30px">

                </div>
            </div>

        </div>
    </section>

    <#include "../footer.ftl">

</main>

</body>

</html>