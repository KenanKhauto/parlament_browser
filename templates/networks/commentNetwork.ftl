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
        <#include "../javascript/commentNetworkGraph.js">
    </script>

    <script>

        let rawData;

        let loadCommentNetwork = function () {
            let sampleSize = document.getElementById("sampleSize").value;

            $("#rl-spinner").addClass("fa-spin");
            $.ajax({
                method: "GET",
                url: "/commentNetworkGraph?sample=" + sampleSize,
                contentType: "application/json",
                success: function (result) {
                    let data = JSON.parse(result);
                    rawData = structuredClone(data);
                    console.log(data);

                    filterAndDraw();

                    $("#rl-spinner").removeClass("fa-spin");
                },
                error: function (error) {
                    $("#comment-network").html(error.responseText);
                }
            });
        }

        let filterAndDraw = function () {
            let data = structuredClone(rawData);
            let types = ["negative", "neutral", "positive"];

            let threshold = document.getElementById("commentThreshold").value;
            data.links = data.links.filter(v => v.value >= threshold);

            let newNodes = new Set();
            data.links.forEach(v => {
                newNodes.add(v.source);
                newNodes.add(v.target);
            })

            data.nodes = [];
            newNodes.forEach(value => {
                data.nodes.push({"id": value})
            })

            let chart = graphDirected({data, types}, {
                /*height: 200,
                width: 400*/
                height: window.innerHeight - 0.15 * window.innerHeight,
                width: window.innerWidth
            });

            $("#comment-network").html(chart);
        }

        let changeThreshold = function () {
            document.getElementById("ThresholdAmount").innerHTML = document.getElementById("commentThreshold").value
        }

        $(document).ready(function () {

            document.getElementById("ThresholdAmount").innerHTML = document.getElementById("commentThreshold").value;
            document.getElementById("commentThreshold").oninput = changeThreshold;

            document.getElementById("Redraw").onclick = filterAndDraw;

            document.getElementById("reloadData").onclick = loadCommentNetwork;

            loadCommentNetwork();

        });

    </script>

</head>
<body>

<#include "../topnav.ftl">
<#include "../sidenav.ftl">

<main>

    <div class="container">
        <h1>Comment Network</h1>
    </div>

    <section>

        <div class="panel container flex" style="gap: 15px; padding-bottom: 0">
            <label>Sample Size:</label>
            <input type="number" min="1" max="1000000" id="sampleSize" value="1000">
            <label>Comments</label>
            <button id="reloadData"><i id="rl-spinner" class="fa-solid fa-rotate"></i></button>
        </div>

        <div class="panel container flex" style="gap: 15px">
            <label>Minimum Comment Frequency</label>
            <input type="range" min="1" max="10" id="commentThreshold">
            <label id="ThresholdAmount" style="width: 20px"></label>
            <button id="Redraw"><i id="rl-spinner" class="fa-solid fa-rotate"></i></button>
        </div>

        <div id="comment-network"
             style="margin: 0 20px; border-radius: 15px; background-color: #333333;
             border: 1px solid rgba(240, 240, 240, 0.2); height: 100%">


        </div>
    </section>

    <#include "../footer.ftl">

</main>

</body>
</html>