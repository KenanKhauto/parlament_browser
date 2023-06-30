<!DOCTYPE html>

<#-- Author: Simon Schuett -->

<html lang="en">

<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>

    <script>
        $(document).ready(function () {

            $("#networks-title-nav").on("click", function () {
                $(this).toggleClass("active");
                $(this).next("ul").toggleClass("active");
            })

        })
    </script>
</head>

<body>

<div class="sidenav">
    <div style="height: 50px; justify-content: center" class="flex">
        <label style="font-weight: bold">Parliament Browser</label>
    </div>
    <div class="line" style="border-color: dimgray; margin: 0 20px"></div>
    <div style="padding: 10px">
        <div>
            <a class="sidenav-btn" href="/">
                <i class="fa-solid fa-home" style="margin-right: 15px"></i>Home
            </a>
        </div>

        <div>
            <h4 style="padding-left: 15px; margin-bottom: 0">Visualisations</h4>
        </div>
        <div>
            <a class="sidenav-btn" href="/graphs">
                <i class="fa-solid fa-chart-column" style="margin-right: 15px"></i>Graphs
            </a>
        </div>
        <div>
            <a class="sidenav-btn" href="/fulltextSpeechSite">
                <i class="fa-solid fa-quote-left" style="margin-right: 15px"></i>Fulltext Speech
            </a>
        </div>
        <div>
            <ul class="collapse-list" id="network-list-nav">
                <li>
                    <span id="networks-title-nav" class="sidenav-btn" style="padding: 10px 15px; border-radius: 10px">
                        <i class="fa-solid fa-circle-nodes" style="margin-right: 15px"></i>Networks
                    </span>
                    <ul style="padding-left: 25px">
                        <li>
                            <span class="" style="padding: 0">
                                <a class="sidenav-btn" href="/commentNetwork" style="padding: 5px 15px"><small>Comments</small></a>
                            </span>
                        </li>
                        <li>
                            <span class="" style="padding: 0">
                                <a class="sidenav-btn" href="/speechTopicNetwork" style="padding: 5px 15px"><small>Speech Topic</small></a>
                            </span>
                        </li>
                        <li>
                            <span class="" style="padding: 0">
                                <a class="sidenav-btn" href="/speechSentimentNetwork" style="padding: 5px 15px"><small>Speech Sentiment</small></a>
                            </span>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>

        <div>
            <h4 style="padding-left: 15px; margin-bottom: 0">Services</h4>
        </div>
        <div>
            <a class="sidenav-btn" href="/latexExport">
                <i class="fa-solid fa-arrow-up-from-bracket" style="margin-right: 15px"></i>PDF Export
            </a>
        </div>

        <div>
            <a class="sidenav-btn" href="/search">
                <i class="fa-solid fa-magnifying-glass" style="margin-right: 15px"></i>Search/Query
            </a>
        </div>
    </div>
</div>

</body>
</html>