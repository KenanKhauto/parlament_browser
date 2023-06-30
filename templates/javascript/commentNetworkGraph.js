// Copyright 2021 Observable, Inc.
// Released under the ISC license.
// https://observablehq.com/@d3/mobile-patent-suits
function graphDirected(
    {
        data,
        /*nodes, // an iterable of node objects (typically [{id}, …])
        links,*/ // an iterable of link objects (typically [{source, target}, …])
        types
    }, {
        nodeId = d => d.id, // given d in nodes, returns a unique identifier (string)
        nodeGroup, // given d in nodes, returns an (ordinal) value for color
        nodeGroups, // an array of ordinal values representing the node groups
        nodeTitle, // given d in nodes, a title string
        nodeFill = "currentColor", // node stroke fill (if not using a group color encoding)
        nodeStroke = "#fff", // node stroke color
        nodeStrokeWidth = 1.5, // node stroke width, in pixels
        nodeStrokeOpacity = 1, // node stroke opacity
        nodeRadius = 5, // node radius, in pixels
        nodeStrength,
        linkSource = ({source}) => source, // given d in links, returns a node identifier string
        linkTarget = ({target}) => target, // given d in links, returns a node identifier string
        linkStroke = "#999", // link stroke color
        linkStrokeOpacity = 0.6, // link stroke opacity
        linkStrokeWidth = 1.5, // given d in links, returns a stroke width in pixels
        linkStrokeLinecap = "round", // link stroke linecap
        linkStrength,
        color = d3.scaleOrdinal(types, /*d3.schemeSet1*/["#e41a1c", "#377eb8", "#4daf4a"]),
        //color = d3.scaleOrdinal(types, d3.schemeCategory10),
        /*colors = d3.schemeTableau10,*/ // an array of color strings, for the node groups
        width = 640, // outer width, in pixels
        height = 400, // outer height, in pixels
        invalidation // when this promise resolves, stop the simulation
    } = {}) {
    const links = data.links.map(d => Object.create(d));
    const nodes = data.nodes.map(d => Object.create(d));


    const simulation = d3.forceSimulation(nodes)
        .force("link", d3.forceLink(links).id(d => d.id).distance(d => 1 * d.value * d.value))
        .force("charge", d3.forceManyBody().strength(-400))
        .force("x", d3.forceX())
        .force("y", d3.forceY());

    const svg = d3.create("svg")
        .attr("viewBox", [-width / 2, -height / 2, width, height])
        .style("font", "12px sans-serif");

    const g = svg.append("g")
        .attr("cursor", "grab");

    // Per-type markers, as they don't inherit styles.
    g.append("defs").selectAll("marker")
        .data(types)
        .join("marker")
        .attr("id", d => `arrow-` + d)
        .attr("viewBox", "0 -5 10 10")
        .attr("refX", 10/*15*/)
        .attr("refY", 0/*-0.5*/)
        .attr("markerWidth", 3/*6*/)
        .attr("markerHeight", 3/*6*/)
        .attr("orient", "auto")
        .append("path")
        .attr("fill", color)
        .attr("d", "M0,-5L10,0L0,5");

    const link = g.append("g")
        .attr("fill", "none")
        .selectAll("path")
        .data(links)
        .join("path")
        .attr("stroke-width", d => d.value/*1.5*/)
        .attr("stroke", d => color(d.type))
        .attr("marker-end", d => `url(` + new URL(`#arrow-` + d.type, location) + `)`);

    const node = g.append("g")
        .attr("fill", "currentColor")
        .attr("stroke-linecap", "round")
        .attr("stroke-linejoin", "round")
        .selectAll("g")
        .data(nodes)
        .join("g")
        .call(drag(simulation));

    node.append("circle")
        .attr("stroke", "white")
        .attr("stroke-width", 1.5)
        .attr("r", 5);

    node.append("text")
        .attr("x", 8)
        .attr("y", "0.31em")
        .text(d => d.id)
    //.clone(true).lower()
    /*.attr("fill", "none")
    .attr("stroke", "white")
    .attr("stroke-width", 3)*/;

    links.forEach(function (d) {
        d.straight = 1;
        links.forEach(function (d1) {
            if ((d.source == d1.target) && (d1.source == d.target))
                d.straight = 0;
        });
    });

    simulation.on("tick", () => {
        link.attr("d", linkArc);
        node.attr("transform", d => `translate( ` + d.x + `,` + d.y + `)`);
    });

    // invalidation.then(() => simulation.stop());

    svg.call(d3.zoom()
        //.extent([[0, 0], [width, height]])
        .scaleExtent([0.01, 8])
        .on("zoom", zoomed));

    function zoomed({transform}) {
        g.attr("transform", transform)
    }

    return svg.node();
}

function linkArc(d) {
    /*const r = Math.hypot(d.target.x - d.source.x, d.target.y - d.source.y);*/
    let dx = d.target.x - d.source.x,
        dy = d.target.y - d.source.y,
        r = (d.straight == 0) ? Math.sqrt(dx * dx + dy * dy) : 0;
    return `
    M` + d.source.x + `,` + d.source.y + `
    A` + r + `,` + r + ` 0 0,1 ` + d.target.x + `,` + d.target.y;
}

drag = simulation => {

    function dragstarted(event, d) {
        if (!event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
    }

    function dragged(event, d) {
        d.fx = event.x;
        d.fy = event.y;
    }

    function dragended(event, d) {
        if (!event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
    }

    return d3.drag()
        .on("start", dragstarted)
        .on("drag", dragged)
        .on("end", dragended);
}
