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

    <script>
        function sendDeputySearchQuery() {
            let firstname = document.getElementById("dep-firstname").value;
            let lastname = document.getElementById("dep-lastname").value;
            let id = document.getElementById("dep-id").value;

            let redirect = "/deputyResults?";

            if (firstname) redirect = redirect + "firstname=" + firstname;
            if (lastname) redirect = redirect + "&lastname=" + lastname;
            if (id) redirect = redirect + "&id=" + id;

            location.href = redirect;
        }

        function sendSpeechSearchQuery() {
            let term = document.getElementById("speech-term").value;
            let id = document.getElementById("speech-id").value;

            let redirect = "/speechResults?";

            if (term) redirect = redirect + "term=" + term;
            if (id) redirect = redirect + "&id=" + id;

            location.href = redirect;
        }

        function sendCommentSearchQuery() {
            let term = document.getElementById("comment-term").value;
            let id = document.getElementById("comment-id").value;

            let redirect = "/commentResults?";

            if (term) redirect = redirect + "term=" + term;
            if (id) redirect = redirect + "&id=" + id;

            location.href = redirect;
        }

    </script>
</head>
<body>

<#include "../topnav.ftl">
<#include "../sidenav.ftl">

<main>

    <div class="container">
        <h1>Search Parliament</h1>
    </div>

    <section>
        <div class="flex container">

            <div class="card" style="min-width: 500px">
                <div class="card-title">
                    <label><i class="fa-solid fa-user-tie" style="margin-right: 15px"></i>Deputy</label>
                </div>
                <div class="card-body">
                    <div class="row">
                        <label class="form-label">Deputy ID</label>
                        <input id="dep-id" class="form-field" type="text">
                    </div>
                    <div class="row">
                        <label class="form-label">Firstname</label>
                        <input id="dep-firstname" class="form-field" type="text">
                    </div>
                    <div class="row">
                        <label class="form-label">Lastname</label>
                        <input id="dep-lastname" class="form-field" type="text">
                    </div>
                    <br>
                    <div class="row">
                        <button onclick="sendDeputySearchQuery()">
                            <i class="fa-solid fa-magnifying-glass" style="margin-right: 10px"></i>Search
                        </button>
                    </div>
                </div>
            </div>

            <div class="card" style="min-width: 500px">
                <div class="card-title">
                    <label><i class="fa-regular fa-comments" style="margin-right: 15px"></i>Speech</label>
                </div>
                <div class="card-body">
                    <div class="row">
                        <label class="form-label">Speech ID</label>
                        <input id="speech-id" class="form-field" type="text">
                    </div>
                    <div class="row">
                        <label class="form-label">Term</label>
                        <input id="speech-term" class="form-field" type="text">
                    </div>
                    <br>
                    <div class="row">
                        <button onclick="sendSpeechSearchQuery()">
                            <i class="fa-solid fa-magnifying-glass" style="margin-right: 10px"></i>Search
                        </button>
                    </div>
                </div>
            </div>

            <div class="card" style="min-width: 500px">
                <div class="card-title">
                    <label><i class="fa-regular fa-comment" style="margin-right: 15px"></i>Comment</label>
                </div>
                <div class="card-body">
                    <div class="row">
                        <label class="form-label">Comment ID</label>
                        <input id="comment-id" class="form-field" type="text">
                    </div>
                    <div class="row">
                        <label class="form-label">Term</label>
                        <input id="comment-term" class="form-field" type="text">
                    </div>
                    <br>
                    <div class="row">
                        <button onclick="sendCommentSearchQuery()">
                            <i class="fa-solid fa-magnifying-glass" style="margin-right: 10px"></i>Search
                        </button>
                    </div>
                </div>
            </div>

        </div>
    </section>

    <#include "../footer.ftl">

</main>

</body>

</html>