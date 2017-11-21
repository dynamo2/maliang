<!DOCTYPE html>
<html lang="en-us">

<head>
    <meta charset="utf-8" />
    <meta name="author" content="Vincent Link, Steffen Lohmann, Eduard Marbach, Stefan Negru, Vitalis Wiens" />
    <meta name="keywords" content="webvowl, vowl, visual notation, web ontology language, owl, rdf, ontology visualization, ontologies, semantic web" />
    <meta name="description" content="WebVOWL - Web-based Visualization of Ontologies" />
    <meta name="robots" content="noindex,nofollow" />
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=1">
    <meta name="apple-mobile-web-app-capable" content="yes">
    
    <!-- jquery -->
		<script src="../js/jquery-2.1.3.min.js"></script>
		<script src="../../js/jquery-ui.min.js"></script>
		<script src="../../js/jquery.layout-latest.js"></script>
		<script src="../js/jquery.simulate.js"></script>
		
    <link rel="stylesheet" type="text/css" href="http://visualdataweb.de/webvowl/css/webvowl.css" />
    <link rel="stylesheet" type="text/css" href="http://visualdataweb.de/webvowl/css/webvowl.app.css" />
    <link rel="icon" href="http://visualdataweb.de/webvowl/favicon.ico" type="image/x-icon" />
    <title>WebVOWL</title>
    <script>
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
      ga('create', 'UA-90312225-2', 'auto');
      ga('send', 'pageview');
    </script>		
</head>

<body>
    <main>
        <section id="canvasArea">
            <div id="browserCheck" class="hidden">
                WebVOWL does not work properly in Internet Explorer and Microsoft Edge. Please use another browser, such as
                <a href="https://www.mozilla.org/de/firefox/">Mozilla Firefox</a> or
                <a href="https://www.google.com/chrome/">Google Chrome</a>, to run WebVOWL.
                <label id="killWarning">Hide warning</label>
            </div>
            <div id="logo">
                <h2>WebVOWL <br/><span>1.0.4</span></h2>
            </div>
            <div id="loading-info">
                <div id="loading-error" class="hidden">
                    <div id="error-info"></div>
                    <div id="error-description-button" class="hidden">Show error details</div>
                    <div id="error-description-container" class="hidden">
                        <pre id="error-description"></pre>
                    </div>
                </div>
                <div id="loading-progress" class="hidden">
                    <span>Loading ontology... </span>
                    <div class="spin">&#8635;</div><br>
                    <div id="myProgress">
                        <div id="myBar"></div>
                    </div>
                </div>
            </div>
            <div id="graph"></div>
        </section>
        <aside id="detailsArea">
            <section id="generalDetails">
                <h1 id="title"></h1>
                <span><a id="about" href=""></a></span>
                <h5>Version: <span id="version"></span></h5>
                <h5>Author(s): <span id="authors"></span></h5>
                <h5>
                    <label>Language: <select id="language" name="language" size="1"></select></label>
                </h5>
                <h3 class="accordion-trigger accordion-trigger-active">Description</h3>
                <div class="accordion-container scrollable">
                    <p id="description"></p>
                </div>
                <h3 class="accordion-trigger">Metadata</h3>
                <div id="ontology-metadata" class="accordion-container"></div>
                <h3 class="accordion-trigger">Statistics</h3>
                <div class="accordion-container">
                    <p class="statisticDetails">Classes: <span id="classCount"></span></p>
                    <p class="statisticDetails">Object prop.: <span id="objectPropertyCount"></span></p>
                    <p class="statisticDetails">Datatype prop.: <span id="datatypePropertyCount"></span></p>
                    <div class="small-whitespace-separator"></div>
                    <p class="statisticDetails">Individuals: <span id="individualCount"></span></p>
                    <div class="small-whitespace-separator"></div>
                    <p class="statisticDetails">Nodes: <span id="nodeCount"></span></p>
                    <p class="statisticDetails">Edges: <span id="edgeCount"></span></p>
                </div>
                <h3 class="accordion-trigger" id="selection-details-trigger">Selection Details</h3>
                <div class="accordion-container" id="selection-details">
                    <div id="classSelectionInformation" class="hidden">
                        <p class="propDetails">Name: <span id="name"></span></p>
                        <p class="propDetails">Type: <span id="typeNode"></span></p>
                        <p class="propDetails">Equiv.: <span id="classEquivUri"></span></p>
                        <p class="propDetails">Disjoint: <span id="disjointNodes"></span></p>
                        <p class="propDetails">Charac.: <span id="classAttributes"></span></p>
                        <p class="propDetails">Individuals: <span id="individuals"></span></p>
                        <p class="propDetails">Description: <span id="nodeDescription"></span></p>
                        <p class="propDetails">Comment: <span id="nodeComment"></span></p>
                    </div>
                    <div id="propertySelectionInformation" class="hidden">
                        <p class="propDetails">Name: <span id="propname"></span></p>
                        <p class="propDetails">Type: <span id="typeProp"></span></p>
                        <p id="inverse" class="propDetails">Inverse: <span></span></p>
                        <p class="propDetails">Domain: <span id="domain"></span></p>
                        <p class="propDetails">Range: <span id="range"></span></p>
                        <p class="propDetails">Subprop.: <span id="subproperties"></span></p>
                        <p class="propDetails">Superprop.: <span id="superproperties"></span></p>
                        <p class="propDetails">Equiv.: <span id="propEquivUri"></span></p>
                        <p id="infoCardinality" class="propDetails">Cardinality: <span></span></p>
                        <p id="minCardinality" class="propDetails">Min. cardinality: <span></span></p>
                        <p id="maxCardinality" class="propDetails">Max. cardinality: <span></span></p>
                        <p class="propDetails">Charac.: <span id="propAttributes"></span></p>
                        <p class="propDetails">Description: <span id="propDescription"></span></p>
                        <p class="propDetails">Comment: <span id="propComment"></span></p>
                    </div>
                    <div id="noSelectionInformation">
                        <p><span>Select an element in the visualization.</span></p>
                    </div>
                </div>
                <textarea id="ttcode" style="width:100%;height:500px;"></textarea>
            </section>
        </aside>
        <nav id="optionsArea">
            <ul id="optionsMenu">
                <li id="aboutMenu"><a href="#">About</a>
                    <ul class="toolTipMenu aboutMenu">
                        <li><a href="license.txt" target="_blank">MIT License &copy; 2014-2017</a></li>
                        <li id="credits" class="option">WebVOWL Developers:<br/> Vincent Link, Steffen Lohmann, Eduard Marbach, Stefan Negru, Vitalis Wiens
                        </li>

                        <li><a href="http://vowl.visualdataweb.org/webvowl.html#releases" target="_blank">Version: 1.0.4<br/>(release history)</a></li>

                        <li><a href="http://purl.org/vowl/" target="_blank">VOWL Specification &raquo;</a></li>
                    </ul>
                </li>
                <li id="pauseOption"><a id="pause-button" href="#">Pause</a></li>
                <li id="resetOption"><a id="reset-button" href="#" type="reset">Reset</a></li>
                <li id="moduleOption"><a href="#">Modes</a>
                    <ul class="toolTipMenu module">
                        <li class="toggleOption" id="dynamicLabelWidth"></li>
                        <li class="toggleOption" id="pickAndPinOption"></li>
                        <li class="toggleOption" id="nodeScalingOption"></li>
                        <li class="toggleOption" id="compactNotationOption"></li>
                        <li class="toggleOption" id="colorExternalsOption"></li>
                    </ul>
                </li>
                <li id="filterOption"><a href="#">Filter</a>
                    <ul class="toolTipMenu filter">
                        <li class="toggleOption" id="datatypeFilteringOption"></li>
                        <li class="toggleOption" id="objectPropertyFilteringOption"></li>
                        <li class="toggleOption" id="subclassFilteringOption"></li>
                        <li class="toggleOption" id="disjointFilteringOption"></li>
                        <li class="toggleOption" id="setOperatorFilteringOption"></li>
                        <li class="slideOption" id="nodeDegreeFilteringOption"></li>
                    </ul>
                </li>
                <li id="gravityOption"><a href="#">Gravity</a>
                    <ul class="toolTipMenu gravity">
                        <li class="slideOption" id="classSliderOption"></li>
                        <li class="slideOption" id="datatypeSliderOption"></li>
                    </ul>
                </li>
                <li id="export"><a href="#">Export</a>
                    <ul class="toolTipMenu export">
                        <li><a href="#" download id="exportJson">Export as JSON</a></li>
                        <li><a href="#" download id="exportSvg">Export as SVG</a></li>
                    </ul>
                </li>
                <li id="select"><a href="#">Ontology</a>
                    <ul class="toolTipMenu select">
                        <li><a href="#foaf" id="foaf">Friend of a Friend (FOAF) vocabulary</a></li>
                        <li><a href="#muto" id="muto">Modular and Unified Tagging Ontology (MUTO)</a></li>
                        <li><a href="#personasonto" id="personasonto">Personas Ontology (PersonasOnto)</a></li>
                        <li><a href="#goodrelations" id="goodrelations">GoodRelations Vocabulary for E-Commerce</a></li>
                        <li><a href="#sioc" id="sioc">SIOC (Semantically-Interlinked Online Communities) Core Ontology</a></li>
                        <li><a href="#ontovibe" id="ontovibe">Ontology Visualization Benchmark (OntoViBe)</a></li>

                        <li class="option" id="converter-option">
                            <form class="converter-form" id="iri-converter-form">
                                <label for="iri-converter-input">Custom Ontology:</label>
                                <input type="text" id="iri-converter-input" placeholder="Enter ontology IRI">
                                <button type="submit" id="iri-converter-button" disabled>Visualize</button>
                            </form>
                            <div class="converter-form">
                                <input class="hidden" type="file" id="file-converter-input" autocomplete="off">
                                <label class="truncate" id="file-converter-label" for="file-converter-input">Select ontology file <small>(max. 10MB)</small></label>
                                <button type="submit" id="file-converter-button" autocomplete="off" disabled>
								Upload
							</button>
                            </div>
                        </li>
                    </ul>
                </li>
                <li id="li_locationSearch"> <a title="Nothing to locate, enter search term." href="#" id="locateSearchResult">&#8853;</a></li>
                <li class="searchMenu" id="searchMenuId">
                    <input class="searchInputText" type="text" id="search-input-text" placeholder="Search">
                    <ul class="searchMenuEntry" id="searchEntryContainer">
                    </ul>
                </li>
                <li id="li_right" style="float:left">
                    <a href="#" id="RightButton"></a>
                </li>
                <li id="li_left" style="float:left">
                    <a href="#" id="LeftButton"></a>
                </li>
            </ul>
            
        </nav>
        
    </main>
    <script src="http://visualdataweb.de/webvowl/js/d3.min.js"></script>
    <script src="/static/json/webvowl/web.js?ddo"></script>
    <script src="/static/json/webvowl/app.js?ddh"></script>
    <script>
        window.onload = webvowl.app().initialize;
    </script>
    
</body>

</html>