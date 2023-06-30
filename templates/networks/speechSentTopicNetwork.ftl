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
        <#include "../javascript/speechTopicNetwork.js">
    </script>

    <script>

        let rawData;

        let loadCommentNetwork = function () {
            let sampleSize = document.getElementById("sampleSize").value;
            let scoreThreshold = getThreshold();

            $("#rl-spinner").addClass("fa-spin");
            $.ajax({
                method: "GET",
                url: "/speechSentTopicGraph?sample=" + sampleSize + "&minScore=" + scoreThreshold,
                contentType: "application/json",
                success: function (result) {
                    let data = JSON.parse(result);
                    rawData = structuredClone(data);
                    console.log(data);

                    filterAndDraw();

                    $("#rl-spinner").removeClass("fa-spin");
                },
                error: function (error) {
                    $("#speechTopic-network").html(error.responseText);
                }
            });
        }

        let filterAndDraw = function () {
            let data = structuredClone(rawData);

            /*let offset = 0.0;
            data.nodes.forEach(node => {
                if (node.group === 0) {
                    node.x = 0;
                } else if (node.group === 2) {
                    node.x = 1000;
                } else {
                    node.x = -1000;
                }
                offset = offset + 1.0;
                node.y = offset;
                console.log(node)
            })
            console.log(data)*/

            let chart = ForceGraph(data, {
                nodeId: d => d.id,
                nodeGroup: d => d.group,
                nodeTitle: d => d.id + `\n` + d.group,
                linkStrokeWidth: l => Math.sqrt(l.value),
                height: window.innerHeight - 0.15 * window.innerHeight,
                width: window.innerWidth,
            });

            $("#speechTopic-network").html(chart);
        }

        let getThreshold = function () {
            return ((1 * document.getElementById("scoreThreshold").value) / 1000);
        }

        let changeThreshold = function () {
            document.getElementById("ThresholdAmount").innerHTML = ((1 * document.getElementById("scoreThreshold").value) / 1000).toString()
        }

        $(document).ready(function () {

            changeThreshold();
            document.getElementById("scoreThreshold").oninput = changeThreshold;

            document.getElementById("reloadData2").onclick = loadCommentNetwork;
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
        <h1>Speech Sentiment and Topic Network</h1>
    </div>

    <section>

        <div class="panel container flex" style="gap: 15px; padding-bottom: 0">
            <label>Sample Size:</label>
            <input type="number" min="1" max="1000000" id="sampleSize" value="500">
            <label>Speeches</label>
            <button id="reloadData"><i id="rl-spinner" class="fa-solid fa-rotate"></i></button>
        </div>

        <div class="panel container flex" style="gap: 15px">
            <label>Minimum Topic Score per Speech</label>
            <input type="range" min="1" max="500" id="scoreThreshold">
            <label id="ThresholdAmount" style="width: 40px"></label>
            <button id="reloadData2"><i id="rl-spinner" class="fa-solid fa-rotate"></i></button>
        </div>

        <div id="speechTopic-network"
             style="margin: 0 20px; border-radius: 15px; background-color: #333333;
             border: 1px solid rgba(240, 240, 240, 0.2); height: 100%">


        </div>
    </section>

    <#include "../footer.ftl">

</main>

</body>
</html>