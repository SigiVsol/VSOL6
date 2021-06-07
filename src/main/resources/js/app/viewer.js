const gray = "#555555", lightGray = "#888888", ultraLightGray = "#cfcfcf";
const blue = "#029ddf", green = "#089d36", red = "#880000";
const lightRed = "#dd7979", yellow = "#928521", orange = "#ffaa00", purple = "#eb00c7";
const /*color1 = "#335c81", */ color2 = "#406869" /*, color3 = "#73937e"*/;

const QUEUE_FIRST = 0, QUEUE_THUMBNAIL = 1, QUEUE_IMAGE = 2;
const SUBMENU_NONE = 0, SUBMENU_ORIENTATIONS = 1, SUBMENU_MEASURES = 2;
const MODAL_NONE = -1, MODAL_LISTVIEWS = 0, MODAL_VIEWPORTS = 1, MODAL_SPLIT = 2, MODAL_DOWNLOAD = 3;
const DOWNLOAD_SELECTED = 0, DOWNLOAD_FLAGGED = 1, DOWNLOAD_ALL = 2;

class Viewer {
    constructor() {
        this.studyColors = [ blue, orange, green, purple, yellow, color2, lightGray, blue, orange, green, purple, yellow, color2, lightGray, blue, orange, green, purple, yellow, color2, lightGray ];

        this.currentSubmenu = SUBMENU_NONE;
        this.currentModal = MODAL_NONE;

        this.viewports = [];
        this.studies = [];
        this.queue = [];
        this.maxSize = 5000;
        this.thumbnailSize = 70;
        this.gridRows = 1;
        this.gridCols = 1;
        this.currentRow = 0;
        this.currentCol = 0;
        this.requestedIndex = -1;
        this.throttle = false;
        this.loading = true;
        this.f6mode = false;
        this.maxListviews = 5;
        this.defaultListviews = 3;
        this.throttleFps = 60;
        this.fps = 30;
        this.draggingStudyIndex = -1;
        this.draggingSerieIndex = -1;
        this.touchDragX = 0;
        this.touchDragY = 0;
        this.downloadInterval = 0;
        this.previousName = "";
        this.code = "";

        this.leftMouseDown = false;
        this.rightMouseDown = false;
        this.singleTouch = false;
        this.doubleTouch = false;
        this.touchBlocker = false;
        this.pinchDistance = 0;
        this.preventSwiping = false;
        this.playControlsPropagation = false;

        this.landscape = true;

        this.btnsListviews = [];
        this.btnsViewports = [];

        this.measurer = new Measurer();

        $(".btnHide").click(() => this.setSubmenu(SUBMENU_NONE));
        $("#divViewports").mouseleave(() => this.highlightViewports(this.gridRows - 1, this.gridCols - 1));
        $("#gridDisplay")
            .on("mousedown", e => {
                if (e.button === 0) this.leftMouseDown = true;
                else if (e.button === 2) this.rightMouseDown = true;
            })
            .on("mouseup", e => {
                if (e.button === 0) this.leftMouseDown = false;
                else if (e.button === 2) this.rightMouseDown = false;
            })
            .on("touchstart", e => {
                if (e.targetTouches.length === 1) {
                    this.singleTouch = true;
                    this.touchBlocker = true;
                } else if (e.targetTouches.length === 2) {
                    this.singleTouch = false;
                    this.doubleTouch = true;
                }
            })
            .on("touchend", () => {
                this.singleTouch = false;
                this.doubleTouch = false;
            }
        );
    }

    show() {
        $("#divLogin").css("display", "none");
        $("#divExplorer").css("display", "none");
        $("#divViewer").css("display", "block");
        $("#divNavbar :button").prop("disabled", false);

        this.loading = true;
        this.viewports.length = 0;

        // this.makeGrid();
        this.fillViewerBar();
        this.fillMenuBar();

        this.setListview(localStorage.getItem("listviewLevel"));

        $("#menuBarTop").html("");

        this.studies.length = 0;
        this.previousName = "";

        this.getStudies();
    }

    getStudies() {
        $.get("app/viewer/getStudies?&id=" + app.id + "&organizationId=" + app.organization.id, json => {
            if (json.code === app.internalCode || json.code === this.code) {
                this.addStudies(json);
                for (let study of this.studies) {
                    this.getSeries(study);
                }
            } else {
                this.code = null;
                // this.fillContent();
                // this.fillNavBar();
                app.pushHistory("clients", null);
                app.show();
            }
        });
    }

    getSeries(study) {
        $.get("app/viewer/getSeries?studyId=" + study.id, json => {
            this.addSeries(study, json);
        });
    }

    addStudies(json) {
        let newStudies = [];

        let studyCounter = this.studies.length;
        for (let jsonStudy of json["studies"]) {
            let study = {
                id: jsonStudy.id, // orthanc study id
                vsol4Id: jsonStudy.vsol4Id, // orthanc study id
                index: studyCounter,
                description: jsonStudy.description,
                name: jsonStudy.name,
                date: jsonStudy.date,
                color: this.studyColors[studyCounter++],
                hidden: false,
                series: []
            };

            newStudies.push(study);
            this.studies.push(study);
        }

        this.addStudiesToMenuBar(newStudies);

        $("#lblLoading").css("display", "none"); // Loading done
        this.loading = false;
    }

    addSeries(study, json) {
        let seriesCounter = 0;
        for (let jsonSerie of json.series) {
            let serie = {
                study: study,
                id: jsonSerie.id,
                index: seriesCounter++,
                flagged: false,

                framecount: jsonSerie.framecount,
                plane: jsonSerie.plane,

                mainDicomTags: jsonSerie.mainDicomTags,

                imagedata: [],
                instances: []
            };

            let instanceCounter = 0;
            for (let jsonInstance of jsonSerie.instances) {
                let instance = {
                    serie: serie,
                    id: jsonInstance.id,
                    index: instanceCounter++,
                    framecount: jsonInstance.framecount,
                    width: jsonInstance.width,
                    height: jsonInstance.height,

                    pdf: jsonInstance.pdf,
                    video: jsonInstance.video,
                    inverted: jsonInstance.inverted,
                    pixelspacing: jsonInstance.pixelspacing,

                    tags: jsonInstance.tags,

                    currentframe: 0,

                    qualities: [],
                    frames: [],
                    shapes: [],
                    rotation: 0,
                    flipped: false
                };
                serie.instances.push(instance);
            }

            study.series.push(serie);
        }

        this.addSeriesToMenuBar(study);
        this.fillQueue(0, 0);
    }

    fillQueue(studyPos, seriePos) {
        this.queue.length = 0;
        let qualities = [200, this.maxSize];

        if (this.studies[studyPos].series.length > 0) {
            // load the very first thumbnail
            this.queue.push( { studyIndex: studyPos, serieIndex: seriePos, type: QUEUE_FIRST } );

            // Load the additional qualities for the current serie, first instance, first frame (to be able to show something already in the viewport)
            for (let q = 0; q < qualities.length; q++) {
                // let frame = studies[studyPos].series[seriePos].instances[0].currentframe;
                this.queue.push( { studyIndex: studyPos, serieIndex: seriePos, instanceIndex: 0, frame: 0, quality: qualities[q], type: QUEUE_IMAGE } );
            }

            // load the other thumbnails
            for (let i = 0; i < this.studies.length; i++) {
                let studyIndex = (Number(studyPos) + i) % this.studies.length;
                for (let j = 0; j < this.studies[studyIndex].series.length; j++) {
                    let serieIndex = (Number(seriePos) + j) % this.studies[studyIndex].series.length;
                    this.queue.push( { studyIndex: studyIndex, serieIndex: serieIndex, type: QUEUE_THUMBNAIL } );
                }
            }

            for (let i = 0; i < this.studies[studyPos].series[seriePos].instances.length; i++) {
                for (let f = 0; f < this.studies[studyPos].series[seriePos].instances[i].framecount; f++) {
                    this.queue.push( { studyIndex: studyPos, serieIndex: seriePos, instanceIndex: i, quality: this.maxSize, frame: f, type: QUEUE_IMAGE } );
                }
            }

            // Load the following series (every frame of every instance of every serie of every study) in full quality
            for (let i = 0; i < this.studies.length; i++) {
                let studyIndex = (Number(studyPos) + i) % this.studies.length;
                for (let j = 0; j < this.studies[studyIndex].series.length; j++) {
                    let serieIndex = (Number(seriePos) + j) % this.studies[studyIndex].series.length;
                    for (let k = 0; k < this.studies[studyIndex].series[serieIndex].instances.length; k++) {
                        for (let f = 0; f < this.studies[studyIndex].series[serieIndex].instances[k].framecount; f++) {
                            this.queue.push( { studyIndex: studyIndex, serieIndex: serieIndex, instanceIndex: k, frame: f, quality: this.maxSize, type: QUEUE_IMAGE } );
                        }
                    }
                }
            }
        }

        this.loadQueue();
    }

    loadQueue() {
        if (this.requestedIndex !== -1) {
            let pos = this.requestedIndex;
            this.requestedIndex = -1;
            this.fillQueue(pos);
            return;
        }

        if (this.queue.length === 0) return;

        let entry = this.queue.shift();

        switch (entry.type) {
            case QUEUE_FIRST:
                this.loadThumbnail(entry.studyIndex, entry.serieIndex, true);
                break;
            case QUEUE_THUMBNAIL:
                this.loadThumbnail(entry.studyIndex, entry.serieIndex, false);
                break;
            case QUEUE_IMAGE:
                this.loadImage(entry.studyIndex, entry.serieIndex, entry.instanceIndex, entry.frame, entry.quality);
                break;
        }
    }

    loadThumbnail(studyIndex, serieIndex, first) {
        let study = this.studies[studyIndex];
        let serie = study.series[serieIndex];

        if (serie.thumbnail != null) {
            this.loadQueue();
            return;
        }

        // console.log("loading thumbnail " + studyIndex + ":" + serieIndex);

        let img = new Image();
        img.src = "app/viewer/getThumbnail?studyId=" + study.id + "&serieId=" + serie.id + "&plane=" + serie.plane;

        let planar = serie.plane === "C" || serie.plane === "S";

        if (planar) { // don't wait for the planar thumbnail, as they require the whole series to be loaded. Proceed with the rest of the queue immediately.
            this.loadQueue();
        }

        img.onload = function() {
            serie.thumbnail = img;
            app.viewer.drawThumbnail(studyIndex, serieIndex);
            if (first) {
                app.viewer.getViewport().studyIndex = -1;
                app.viewer.getViewport().serieIndex = -1;
                app.viewer.selectSerie(studyIndex, serieIndex);
            }

            if (!planar) { // in every other case, wait for the thumbnail to be loaded before proceeding with the rest of the queue
                app.viewer.loadQueue();
            }
        }
    }

    loadImage(studyIndex, serieIndex, instanceIndex, frame, quality) {
        let study = this.studies[studyIndex];
        let serie = study.series[serieIndex];
        let instance = serie.instances[instanceIndex];

        if (instance.qualities[frame] >= quality || instance.pdf || instance.video) {
            this.loadQueue();
        } else {
            // console.log("loading image " + studyIndex + ":" + serieIndex + ":" + instanceIndex + " frame " + frame + " quality " + quality);

            let img = new Image();
            img.src = "app/viewer/getImage?studyId=" + study.id + "&serieId=" + serie.id + "&instanceId=" + instance.id + "&frame=" + frame + "&quality=" + quality + "&plane=" + serie.plane;

            img.onload = function() {
                if (instance.qualities[frame] == null || instance.qualities[frame] < quality) {
                    instance.qualities[frame] = quality;

                    instance.frames[frame] = img;
                    if (app.viewer.isActive(studyIndex, serieIndex)) {
                        app.viewer.fitImages();
                    }

                    app.viewer.loadQueue();
                }
            }
        }
    }

    getStudy(viewport) {
        let studyIndex = viewport.studyIndex;

        if (studyIndex >= 0)
            return this.studies[studyIndex];
        else
            return null;
    }

    getSerie(viewport) {
        let studyIndex = viewport.studyIndex;
        let serieIndex = viewport.serieIndex;

        if (studyIndex >= 0 && serieIndex >= 0)
            return this.studies[studyIndex].series[serieIndex];
        else
            return null;
    }

    getInstance(viewport) {
        let studyIndex = viewport.studyIndex;
        let serieIndex = viewport.serieIndex;
        let instanceIndex = viewport.instanceIndex;

        if (studyIndex >= 0 && serieIndex >= 0 && instanceIndex >= 0)
            return this.studies[studyIndex].series[serieIndex].instances[instanceIndex];
        else
            return null;
    }

    getActiveStudy() {
        if (this.currentRow >= 0 && this.currentCol >= 0)
            return this.getStudy(this.viewports[this.currentRow][this.currentCol]);
        else return null;
    }

    getActiveSerie() {
        if (this.currentRow >= 0 && this.currentCol >= 0)
            return this.getSerie(this.viewports[this.currentRow][this.currentCol]);
        else return null;
    }

    getActiveInstance() {
        if (this.currentRow >= 0 && this.currentCol >= 0)
            return this.getInstance(this.viewports[this.currentRow][this.currentCol]);
        else return null;
    }

    // viewerBar

    fillViewerBar() {
        $("#btnInfo").css("background-color", localStorage.getItem("showInfo") ? "var(--blue)" : "black");
        $("#btnSwipe").css("background-color", localStorage.getItem("swiping") ? "var(--blue)" : "black");
        $("#btnWindow").css("background-color", localStorage.getItem("swiping") ? "black" : "var(--blue)");

        $("#btnSplit").attr("disabled", app.internalCode == null);
        $("#btnSaveCopy").attr("disabled", app.internalCode != null || app.user.id == null);
    }

    next() {
        let viewport = this.getViewport();
        let studyIndex = viewport.studyIndex;
        let serieIndex = viewport.serieIndex;
        if (studyIndex < 0 || serieIndex < 0) return;

        let nextStudyIndex = studyIndex;
        let nextSerieIndex = (serieIndex + 1) % this.studies[studyIndex].series.length;
        if (nextSerieIndex === 0) nextStudyIndex = (studyIndex + 1) % this.studies.length;

        if (viewport.studyIndex === nextStudyIndex && viewport.serieIndex === nextSerieIndex) return;

        viewport.studyIndex = nextStudyIndex;
        viewport.serieIndex = nextSerieIndex;

        this.fitImage(viewport);

        this.drawThumbnail(studyIndex, serieIndex);
        this.drawThumbnail(nextStudyIndex, nextSerieIndex);

        if (this.landscape) this.studies[nextStudyIndex].series[nextSerieIndex].thumbnailCanvas.scrollIntoView({ behavior: "smooth", block: "end" });
        else this.studies[nextStudyIndex].series[nextSerieIndex].thumbnailCanvas.scrollIntoView();
    }

    previous() {
        let viewport = this.getViewport();
        let studyIndex = viewport.studyIndex;
        let serieIndex = viewport.serieIndex;
        if (studyIndex < 0 || serieIndex < 0) return;

        let previousStudyIndex = studyIndex;
        let previousSerieIndex = serieIndex - 1;

        if (previousSerieIndex === -1) {
            previousStudyIndex = (studyIndex + studies.length - 1) % studies.length;
            previousSerieIndex = studies[previousStudyIndex].series.length - 1;
        }

        if (viewport.studyIndex === previousStudyIndex && viewport.serieIndex === previousSerieIndex) return;

        viewport.studyIndex = previousStudyIndex;
        viewport.serieIndex = previousSerieIndex;

        this.fitImage(viewport);

        this.drawThumbnail(studyIndex, serieIndex);
        this.drawThumbnail(previousStudyIndex, previousSerieIndex);

        if (this.landscape) this.studies[previousStudyIndex].series[previousSerieIndex].thumbnailCanvas.scrollIntoView({ behavior: "smooth", block: "end" });
        else this.studies[previousStudyIndex].series[previousSerieIndex].thumbnailCanvas.scrollIntoView();
    }

    toggleInfo() {
        if (localStorage.getItem("showInfo")) {
            localStorage.removeItem("showInfo");
        } else {
            localStorage.setItem("showInfo", "true");
        }
        this.fillViewerBar();
        this.drawImages();
    }

    toggleSwiping() {
        if (localStorage.getItem("swiping")) {
            localStorage.removeItem("swiping");
        } else {
            localStorage.setItem("swiping", "true");
        }
        this.fillViewerBar();
    }

    toggleLens() {
        if (localStorage.getItem("lens")) {
            localStorage.removeItem("lens");
        } else {
            localStorage.setItem("lens", "true");
        }
        this.fillMenuBar();
        this.drawImages();
    }

    toggleInverted() {
        if (localStorage.getItem("inverted")) {
            localStorage.removeItem("inverted");
        } else {
            localStorage.setItem("inverted", "true");
        }
        this.fillMenuBar();
        this.drawImages();
    }

    setModal(modal) {
        const btnsModal = [ $("#btnListviews"), $("#btnViewports"), $("#btnSplit"), $("#btnDownload") ];
        const divsModal = [ $("#divListviews"), $("#divViewports"), $("#divSplit"), $("#divDownload") ];

        if (this.currentModal === modal) modal = MODAL_NONE;
        this.currentModal = modal;
        for (let div of divsModal) div.css("visibility", "hidden");
        for (let btn of btnsModal) btn.removeClass("btnSelected");
        if (modal > -1) divsModal[modal].css("visibility", "visible");
        if (modal > -1) btnsModal[modal].addClass("btnSelected");

        btnsModal[1].addClass("selected");
    }

    showListviewDialog() {
        const divListviews = $("#divListviews");

        if (divListviews.html() === "") {
            divListviews.html("<p class='lblModalTitle'>Thumbnails</p>");
            for (let i = this.maxListviews; i >= 0; i--) {
                let div = $("<div style='margin-bottom: 5px; text-align: right'></div>").appendTo(divListviews);
                let btn = $("<button class='btnListview'></button>").appendTo(div);

                if (i === 3)
                    $("<p class='lblListview'><b>3</b> per row (default)</p>").appendTo(btn);
                else if (i > 0)
                    $("<p class='lblListview'><b>" + i + "</b> per row</p>").appendTo(btn);
                else
                    $("<p class='lblListview'><b>hide</b> toolbar</p>").appendTo(btn);

                btn.click(function() { app.viewer.setListview(i); });
                $("#btnsListviews")[i] = btn;
            }
        } else {
            for (let i = this.maxListviews; i >= 0; i--) {
                this.btnsListviews[i].removeClass("btnSelected");
            }
        }

        divListviews[localStorage.getItem("listviewLevel")].addClass("btnSelected");

        this.setModal(MODAL_LISTVIEWS);
    }

    setListview(listviewLevel) {
        if (listviewLevel == null || isNaN(listviewLevel)) listviewLevel = 3;

        if (listviewLevel > this.maxListviews) return;

        if (this.landscape) {
            if (listviewLevel === 0) this.setMenuBarWidth(0);
            else this.setMenuBarWidth(listviewLevel * 70 + (listviewLevel - 1) * 2 + 46);
        }

        localStorage.setItem("listviewLevel", listviewLevel.toString());
        this.setModal(MODAL_NONE);

        this.makeGrid();
    }

    setMenuBarWidth(width) {
        $(':root').css("--menuBarWidth", width + "px");
    }

    showViewportsDialog() {
        const divViewports = $("#divViewports");

        divViewports.html("");

        let maxRows = this.landscape ? 3 : 2;
        let maxCols = this.landscape ? 4 : 2;

        divViewports.html("<p class='lblModalTitle'>Viewports</p>");
        for (let i = 0; i < maxRows; i++) {
            let divTemp = $("<div style='margin: 5px'></div>").appendTo(divViewports);

            this.btnsViewports[i] = [];

            for (let j = 0; j < maxCols; j++) {
                this.btnsViewports[i][j] = $("<button class='btnViewports'></button>").appendTo(divTemp);
                divTemp.append(" ");

                this.btnsViewports[i][j].mouseover(function() {
                    app.viewer.highlightViewports(i, j);
                });

                this.btnsViewports[i][j].click(function() {
                    this.gridRows = i + 1;
                    this.gridCols = j + 1;
                    app.viewer.setModal(MODAL_NONE);
                    app.viewer.makeGrid();
                });
            }
        }

        this.highlightViewports(this.gridRows - 1, this.gridCols - 1);

        this.setModal(MODAL_VIEWPORTS);
    }

    highlightViewports(row, col) {
        $(".btnViewports").removeClass("btnSelected");
        for (let i = 0; i <= row; i++) {
            for (let j = 0; j <= col; j++) {
                this.btnsViewports[i][j].addClass("btnSelected");
            }
        }
    }

    split(description, flagged) {
        let data = {
            description: description,
            studies: []
        };

        for (let study of this.studies) {
            let entry = {
                vsol4Id: study.vsol4Id,
                id: study.id,
                series: []
            };
            for (let serie of study.series) {
                if (serie.flagged === flagged) {
                    entry.series.push( { id: serie.id } );
                }
            }
            if (entry.series.length > 0) {
                data.studies.push(entry);
            }
        }

        $.post("app/viewer/getSplit?organizationId=" + app.organization.id, JSON.stringify(data), json => {
            this.addStudies(json);

            let count = json["studies"].length;

            for (let i = this.studies.length - count; i <= this.studies.length - 1; i++) {
                this.getSeries(this.studies[i]);
            }

            for (let study of json["studies"]) {
                app.id += "," + study.vsol4Id;
            }
            app.replaceHistory();
        });
    }

    showSplitDialog() {
        this.setModal(MODAL_SPLIT);
    }

    showDownloadDialog() {
        this.setModal(MODAL_DOWNLOAD);
    }

    highlightSplitAndDownload() {
        let present = 0;
        let selected = 0;
        let flagged = 0;
        let unflagged = 0;

        for (let study of this.studies) {
            for (let serie of study.series) {
                present++;

                if (this.isActive(study.index, serie.index)) selected++;
                if (serie.flagged) flagged++;
                if (!serie.flagged) unflagged++;
            }
        }

        $("#btnSplitFlagged").attr("disabled", !(flagged && unflagged));
        $("#btnSplitNonFlagged").attr("disabled", !(flagged && unflagged));
        $("#btnSplitBoth").attr("disabled", !(flagged && unflagged));

        $("#btnDownloadSelected").attr("disabled", !selected);
        $("#btnDownloadFlagged").attr("disabled", !flagged);
        $("#btnDownloadAll").attr("disabled", !present);

        $(".lblCountFlagged").text(flagged);
        $(".lblCountUnflagged").text(unflagged);
        $(".lblCountSelected").text(selected);
        $(".lblCountSeries").text(present);
    }

    download(mode) {
        let data = {
            studies: []
        };

        for (let study of this.studies) {
            let entry = {
                id: study.id,
                series: []
            };

            for (let serie of study.series) {
                if (mode === DOWNLOAD_ALL || (mode === DOWNLOAD_FLAGGED && serie.flagged) || (mode === DOWNLOAD_SELECTED && isActive(study.index, serie.index))) {
                    entry.series.push( { id: serie.id } );
                }
            }

            if (entry.series.length > 0) {
                data.studies.push(entry);
            }
        }

        $.post("app/viewer/prepareDownload", JSON.stringify(data), json => {
            let url = "app/getDownload?code=" + json.code;
            app.autoStartDownload(url);
            this.setModal(MODAL_NONE);
        });
    }

    saveCopy() {
        let text = "Save a copy in " + $("#lblOrganizationName").text() + "?";

        Dialog.confirm(text, () => {
            let data = {
                description: "Imported",
                studies: []
            };

            for (let study of this.studies) {
                let entry = {
                    vsol4Id: study.vsol4Id,
                    id: study.id,
                    series: []
                };
                for (let serie of study.series) {
                    entry.series.push( { id: serie.id } );
                }
                data.studies.push(entry);
            }

            $.post("app/viewer/saveCopy?organizationId=" + app.organization.id, JSON.stringify(data), json => {
                let idList = "";
                for (let study of json.studies) {
                    idList += study.vsol4Id + ",";
                }

                app.internalCode = json.code;
                this.code = ""; // TODO check this for code
                app.pushHistory("viewer", idList.substr(0, idList.length - 1));
                app.show();
            });
        });
    }

    reopen() {
        let study = this.getStudy(this.getViewport());
        if (study == null) study = this.studies[0];

        $.get("command/reopen?studyid=" + study.vsol4Id); // TODO
    }

    sendDicom() {
        let serie = this.getActiveSerie();

        $.get("request/send?studyId=" + serie.study.id + "&serieId=" + serie.id, json => { // TODO
            Dialog.inform("Dicom file sent to " + json["DicomDestinationAET"] + ".");
        })
    }

    // menuBar

    fillMenuBar() {
        $("#btnInvert").css("background-color", localStorage.getItem("inverted") ? "var(--blue)" : "black");
        $("#btnLens").css("background-color", localStorage.getItem("lens") ? "var(--blue)" : "black");
    }

    addStudiesToMenuBar(newStudies) {
        const menuBarTop = $("#menuBarTop");

        for (let study of newStudies) {
            if (study.name !== this.previousName) {
                let margin = this.previousName === "" ? "10px 0 10px 0" : "30px 0 10px 0";
                this.previousName = study.name;
                $("<p class='studyName landscapeOnly' style='margin: " + margin + "' onclick='hideStudyByName(" + study.index +  ")'>" + study.name + "</p>").appendTo(menuBarTop);
            }

            let divStudy = $("<div class='divStudy' style='border-color: " + study.color + "'></div>").appendTo(menuBarTop);

            let description = (study.description);
            if (app.internalCode == null) { // it's a public study
                description = "";
            }
            if (description !== "") description += " &middot; ";
            description += study.date;

            $("<p class='studyDescription landscapeOnly' style='background-color: " + study.color + "' onclick='hideStudyByIndex(" + study.index + ")'>" + description + "</p>").appendTo(divStudy);

            $("<div id='divThumbnails_" + study.id + "' class='divThumbnails divThumbnailsStudyIndex_" + study.index + " divThumbnailsStudyName_" + study.name.replace(" ", "") + "' style='border-color: " + study.color + "'></div>").appendTo(divStudy);
        }
    }

    addSeriesToMenuBar(study) {
        const touchDragger = $("#touchDragger");
        let divThumbnails = $("#divThumbnails_" + study.id);

        for (let serie of study.series) {
            let canvas = $("<canvas class='thumbnailCanvas' onclick='selectSerie(" + study.index + ", " + serie.index + ")' draggable='true' ondragstart='dragSerie(" + study.index + ", " + serie.index + ")' ondragend='stopDragging()'></canvas>").appendTo(divThumbnails);
            serie.thumbnailCanvas = canvas[0];
            if (serie.thumbnailCanvas == null) continue;

            serie.thumbnailCanvas.width = 70;
            serie.thumbnailCanvas.height = 70;

            canvas.on("touchstart", e => {
                this.touchDragX = e.targetTouches[0].clientX;
                this.touchDragY = e.targetTouches[0].clientY;
            });

            canvas.on("touchmove", e => {
                let x = e.targetTouches[0].clientX;
                let y = e.targetTouches[0].clientY;

                let enable = this.landscape ? (this.touchDragX - x > 20) : (this.touchDragY - y > 20);

                if (enable) {
                    touchDragger.css("visibility", "visible");
                    touchDragger.css("left", (x - 35) + "px");
                    touchDragger.css("top", (y - 35) + "px");
                }
            });

            canvas.on("touchend", e => {
                touchDragger.css("visibility", "hidden");
                let x = e.changedTouches[0].clientX;
                let y = e.changedTouches[0].clientY;

                if (this.gridRows === 1 && this.gridCols === 1) {
                    let rect = { x: 0, y:0, width: this.getInnerWidth(), height: this.getInnerHeight() };
                    if (this.contains(rect, x, y)) {
                        this.selectSerie(study.index, serie.index, true);
                    }
                } else {
                    outerloop:
                        for (let i = 0; i < this.gridRows; i++) {
                            for (let j = 0; j < this.gridCols; j++) {
                                let canvas = this.viewports[i][j].canvas[0];

                                if (this.contains(canvas.getBoundingClientRect(), x, y)) {
                                    let previousRow = this.currentRow;
                                    let previousCol = this.currentCol;

                                    this.currentRow = i;
                                    this.currentCol = j;

                                    if (previousRow >= 0 && previousCol >= 0) {
                                        this.drawImage(this.viewports[previousRow][previousCol]);
                                        this.drawThumbnail(this.viewports[previousRow][previousCol].studyIndex, this.viewports[previousRow][previousCol].serieIndex);
                                    }

                                    this.drawImage(this.viewports[this.currentRow][this.currentCol]);
                                    this.drawThumbnail(this.viewports[this.currentRow][this.currentCol].studyIndex, this.viewports[this.currentRow][this.currentCol].serieIndex);

                                    this.selectSerie(study.index, serie.index, true);
                                    break outerloop;
                                }
                            }
                        }
                }
            });
        }

        this.enableNextPrevious();
    }

    drawThumbnail(studyIndex, serieIndex) {
        if (studyIndex < 0 || serieIndex < 0) return;

        let study = this.studies[studyIndex];
        let serie = study.series[serieIndex];

        // if (serie.thumbnailCanvas == null) return;

        let gg = serie.thumbnailCanvas.getContext('2d');
        let thumbnail = serie.thumbnail;

        gg.clearRect(0, 0, this.thumbnailSize, this.thumbnailSize);

        if (thumbnail != null) {
            let iw = thumbnail.width;
            let ih = thumbnail.height;

            let relativeWidth = iw / this.thumbnailSize;
            let relativeHeight = ih / this.thumbnailSize;

            let max = Math.max(relativeWidth, relativeHeight);
            let scale = 1 / max;

            let offsetX = (this.thumbnailSize - iw) / 2;
            let offsetY = (this.thumbnailSize - ih) / 2;

            gg.save();

            gg.translate(offsetX, offsetY);

            gg.translate(iw / 2, ih / 2);
            gg.scale(scale, scale);
            gg.translate(-iw / 2, -ih / 2);

            gg.drawImage(thumbnail, 0, 0);

            gg.restore();
        }

        if (serie.framecount > 1) {
            gg.fillStyle = study.color;
            gg.font = "bold 12px Verdana";
            gg.textBaseline = "bottom";
            gg.textAlign = "right";
            gg.fillText(serie.framecount, 65, 68);
        }

        if (serie.plane !== undefined) {
            gg.fillStyle = study.color;
            gg.font = "bold 16px Verdana";
            gg.textBaseline = "top";
            gg.textAlign = "left";
            gg.fillText(serie.plane, 5, 5);
        }

        if (this.isShown(studyIndex, serieIndex)) {
            gg.strokeStyle = study.color;
            if (this.isActive(studyIndex, serieIndex)) gg.setLineDash([]);
            else gg.setLineDash([5,5]);
            gg.lineWidth = 4;
            gg.strokeRect(0, 0, this.thumbnailSize, this.thumbnailSize);
        }

        if (serie.flagged && this.landscape) {
            gg.fillStyle = study.color;
            gg.beginPath();
            gg.moveTo(0, 0);
            gg.lineTo(this.thumbnailSize / 3, 0);
            gg.lineTo(0, this.thumbnailSize / 3);
            gg.closePath();
            gg.fill();
        }
    }

    selectSerie(studyIndex, serieIndex, dragging = false) {
        let study = this.studies[studyIndex];
        let serie = study.series[serieIndex];

        if (!dragging && this.isActive(studyIndex, serieIndex)) {
            serie.flagged = !serie.flagged;
            this.drawThumbnail(studyIndex, serieIndex);
        } else {
            let viewport = this.getViewport();

            let previousStudyIndex = viewport.studyIndex;
            let previousSerieIndex = viewport.serieIndex;

            viewport.studyIndex = studyIndex;
            viewport.serieIndex = serieIndex;
            viewport.instanceIndex = 0;

            if (previousStudyIndex !== -1) this.drawThumbnail(previousStudyIndex, previousSerieIndex);
            this.drawThumbnail(studyIndex, serieIndex);

            this.fitImage(viewport);
            this.drawImages();
        }

        this.fillQueue(studyIndex, serieIndex);

        this.highlightSplitAndDownload();
    }

    fillOrientations(viewport) {
        const btnFlip = $("#btnFlip");
        const btnMirror = $("#btnMirror");
        const btnFit = $("#btnFit");

        let instance = this.getInstance(viewport);
        let scale = instance == null ? 0 : instance.scale;

        $("#lblZoom").text((scale * 100).toLocaleString(undefined, {maximumFractionDigits: 1, minimumFractionDigits: 1}) + " %");

        let rotation = 0;
        let flipped = false;
        if (instance != null) {
            rotation = instance.rotation;
            flipped = instance.flipped;
        }

        $("#lblRotation").text((rotation === undefined ? "0" : rotation) + " °" + (flipped ? " (%{mirrored})" : ""));

        if (rotation === 0 || rotation === 180) {
            btnFlip.css("display", "none");
            btnMirror.css("display", "inline");
        } else if (rotation === 90 || rotation === 270) {
            btnFlip.css("display", "inline");
            btnMirror.css("display", "none");
        }

        if (scale === viewport.fitscale) {
            btnFit.addClass("btnSelected");
        } else {
            btnFit.removeClass("btnSelected");
        }

        if (flipped) {
            btnMirror.addClass("btnSelected");
            btnFlip.addClass("btnSelected");
        } else {
            btnMirror.removeClass("btnSelected");
            btnFlip.removeClass("btnSelected");
        }
    }

    setSubmenu(submenu) {
        const menuBarTop = $("#menuBarTop");
        const btnsSubmenu = [ null, $("#btnOrientations"), $("#btnMeasures") ];
        const divsSubmenu = [ null, $("#divOrientations"), $("#divMeasures") ];

        if (this.currentSubmenu === submenu) submenu = SUBMENU_NONE;

        let listviewLevel = parseInt(localStorage.getItem("listviewLevel"));

        if (submenu === SUBMENU_ORIENTATIONS && listviewLevel < 2) this.setListview(2);
        else if (submenu === SUBMENU_MEASURES && listviewLevel < 3) this.setListview(3);

        this.currentSubmenu = submenu;
        for (let btn of btnsSubmenu) { if (btn != null) btn.removeClass("btnViewerBarActiveTab"); }
        for (let div of divsSubmenu) { if (div != null) div.css("display", "none"); }

        if (submenu > 0) {
            divsSubmenu[submenu].css("display", "block");
            btnsSubmenu[submenu].addClass("btnViewerBarActiveTab");
            let height = divsSubmenu[submenu].css("height");
            menuBarTop.css("height", "calc(100% - " + height + ")");
        } else {
            menuBarTop.css("height", "calc(100%)");
        }

        this.setModal(MODAL_NONE);
        this.drawImage(this.getViewport());
    }

    zoomIn()  {
        let viewport = this.getViewport();
        let instance = this.getInstance(viewport);
        instance.scale *= 1.1;
        this.drawImage(viewport);
    }

    zoomOut() {
        let viewport = this.getViewport();
        let instance = this.getInstance(viewport);
        instance.scale /= 1.1;
        this.drawImage(viewport);
    }

    zoomToFit() {
        let viewport = this.getViewport();
        let instance = this.getInstance(viewport);
        instance.scale = viewport.fitscale;
        viewport.offsetx = viewport.fitx;
        viewport.offsety = viewport.fity;
        this.drawImage(viewport);
    }

    zoomTo100() {
        let viewport = this.getViewport();
        let instance = this.getInstance(viewport);
        instance.scale = 1.0;
        viewport.offsetx = viewport.fitx;
        viewport.offsety = viewport.fity;
        this.drawImage(viewport);
    }

    rotateTo0() {
        let viewport = this.getViewport();
        let instance = this.getInstance(viewport);
        instance.rotation = 0;
        instance.flipped = false;
        this.drawImage(viewport);
    }

    rotateLeft() {
        let viewport = this.getViewport();
        let instance = this.getInstance(viewport);
        instance.rotation = (instance.rotation + 270) % 360;
        this.drawImage(viewport);
    }

    rotateRight() {
        let viewport = this.getViewport();
        let instance = this.getInstance(viewport);
        instance.rotation = (instance.rotation + 90) % 360;
        this.drawImage(viewport);
    }

    mirror() {
        let viewport = this.getViewport();
        let instance = this.getInstance(viewport);
        instance.flipped = !instance.flipped;
        this.drawImage(viewport);
    }

    flip() {
        let viewport = this.getViewport();
        let instance = this.getInstance(viewport);
        instance.flipped = !instance.flipped;
        this.drawImage(viewport);
    }

    enableNextPrevious() {
        let seriesCounter = 0;
        for (let study of this.studies) {
            seriesCounter += study.series.length;
        }

        $("#btnNext").attr("disabled", seriesCounter <= 1);
        $("#btnPrevious").attr("disabled", seriesCounter <= 1);
    }

    dragSerie(studyIndex, serieIndex) {
        this.draggingStudyIndex = studyIndex;
        this.draggingSerieIndex = serieIndex;
    }

    stopDragging() {
        this.draggingStudyIndex = -1;
        this.draggingSerieIndex = -1;
    }

    hideStudyByIndex(studyIndex) {
        let div = $(".divThumbnailsStudyIndex_" + studyIndex);

        if (div.css("display") === "block") {
            div.css("display", "none");
            div.css("margin-top", "0");
        }
        else {
            div.css("display", "block");
            div.css("margin-top", "3px");
        }
    }

    hideStudyByName(studyIndex) {
        let name = this.studies[studyIndex].name.replace(" ", "");
        let div = $(".divThumbnailsStudyName_" + name);

        if (div.css("display") === "block") {
            div.css("display", "none");
            div.css("margin-top", "0");
        }
        else {
            div.css("display", "block");
            div.css("margin-top", "3px");
        }
    }

    showPlanar() {
        let viewport = this.getViewport();
        let study = this.studies[viewport.studyIndex];
        let serie = study.series[viewport.serieIndex];

        if (serie.plane === "A") {
            this.gridRows = 1;
            this.gridCols = 3;
            this.setModal(MODAL_NONE);

            this.makeGrid();

            this.viewports[0][1].studyIndex = this.getViewport().studyIndex;
            this.viewports[0][1].serieIndex = this.getViewport().serieIndex + 1;
            this.viewports[0][1].instanceIndex = 0;
            this.fitImage(this.viewports[0][1]);

            this.viewports[0][2].studyIndex = this.getViewport().studyIndex;
            this.viewports[0][2].serieIndex = this.getViewport().serieIndex + 2;
            this.viewports[0][2].instanceIndex = 0;
            this.fitImage(this.viewports[0][2]);

            this.drawImages();
        }
    }
    
    // display

    getDisplayWidth() {
        if (!this.landscape || (this.gridRows === 1 && this.gridCols === 1) ) {
            return $("#divContent").width();
        } else {
            return this.getInnerWidth();
        }
    }

    getDisplayHeight() {
        if (this.landscape || (this.gridRows === 1 && this.gridCols === 1)) {
            return $("#divContent").height();
        } else {
            return this.getInnerHeight();
        }
    }

    getInnerWidth() {
        return $("#divContent").width() - (this.landscape ? ($("#divMenuBar").width() - 80) : 0);
    }

    getInnerHeight() {
        return $("#divContent").height() - (this.landscape ? 0 : ($("#divMenuBar").height() - 73));
    }

    getViewport() {
        return this.viewports[this.currentRow][this.currentCol];
    }

    makeGrid() {
        const gridDisplay = $("#gridDisplay");
        let displayWidth = this.getDisplayWidth();
        let displayHeight = this.getDisplayHeight();

        gridDisplay.css("width", displayWidth);
        gridDisplay.css("height", displayHeight);

        let previousIndices = this.getShownIndices(); // to refill the grid if there were already images visible

        this.viewports.length = 0;

        let gridTemplateColumns = "";
        for (let j = 0; j < this.gridCols; j++) gridTemplateColumns += "1fr ";
        gridDisplay.css("grid-template-columns", gridTemplateColumns);

        gridDisplay.html("");
        let gridItems = [];
        for (let i = 0; i < this.gridRows; i++) {
            gridItems[i] = [];
            for (let j = 0; j < this.gridCols; j++) {
                gridItems[i][j] = $("<div class='gridItem'>").appendTo(gridDisplay);
            }
        }

        let previousIndexCounter = 0;

        for (let i = 0; i < this.gridRows; i++) {
            this.viewports[i] = [];
            for (let j = 0; j < this.gridCols; j++) {
                gridItems[i][j].css("display", "block");
                gridItems[i][j].css("position", "relative");

                let imageCanvas = $("<canvas width='" + (displayWidth / this.gridCols) + "' height='" + (displayHeight / this.gridRows) + "' style='position: absolute'></canvas>").appendTo(gridItems[i][j]);
                let overlayCanvas = $("<canvas width='" + (this.getInnerWidth() / this.gridCols) + "' height='" + (this.getInnerHeight() / this.gridRows) + "' style='position: absolute'></canvas>").appendTo(gridItems[i][j]);
                let video = $("<video width='" + (this.getInnerWidth() / this.gridCols) + "' height='" + (this.getInnerHeight() / this.gridRows) + "' style='position: absolute; display: none' controls></video>").appendTo(gridItems[i][j]);
                let pdf = $("<iframe width='" + (this.getInnerWidth() / this.gridCols) + "' height='" + (this.getInnerHeight() / this.gridRows) + "' style='position: absolute; display: none; border: none'></iframe>").appendTo(gridItems[i][j]);
                let divPlayControls = $("<div class='divPlayControls'></div>").appendTo(gridItems[i][j]);
                let btnRewind = $("<button class='btnPlayControls' onclick='rewind(" + i + ", " + j + ")'><img src='../icon/rewind/16' alt=''></button>").appendTo(divPlayControls);
                divPlayControls.append(" ");
                let btnPlayBack = $("<button class='btnPlayControls' onclick='play(" + i + ", " + j + ", false)'><img src='../icon/playback/16' alt=''></button>").appendTo(divPlayControls);
                let btnPauseBack = $("<button class='btnPlayControls' onclick='stop(" + i + ", " + j + ")' style='display: none'><img src='../icon/pause/16' alt=''></button>").appendTo(divPlayControls);
                divPlayControls.append(" ");
                let btnStepBack = $("<button class='btnPlayControls' onclick='step(" + i + ", " + j + ", -1)'><img src='../icon/previous/16' alt=''></button>").appendTo(divPlayControls);
                divPlayControls.append(" ");
                let btnStep = $("<button class='btnPlayControls' onclick='step(" + i + ", " + j + ", +1)'><img src='../icon/next/16' alt=''></button>").appendTo(divPlayControls);
                divPlayControls.append(" ");
                let btnPlay = $("<button class='btnPlayControls'><img src='../icon/play/16' alt='' onclick='play(" + i + ", " + j + ", true)'></button>").appendTo(divPlayControls);
                let btnPause = $("<button class='btnPlayControls' onclick='stop(" + i + ", " + j + ")' style='display: none'><img src='../icon/pause/16' alt=''></button>").appendTo(divPlayControls);
                divPlayControls.append(" ");
                let lblPlayControls = $("<p class='lblPlayControls' style='text-align: center'></p>").appendTo(divPlayControls);

                divPlayControls.css("left", "calc(" + (this.getInnerWidth() / this.gridCols) +  "px / 2 - " + divPlayControls.css("width") + " / 2)");
                if (!this.landscape && this.gridRows === 1 && this.gridCols === 1) divPlayControls.css("bottom", this.getDisplayHeight() - this.getInnerHeight());
                else divPlayControls.css("bottom", "10px");

                let studyIndex = -1;
                let serieIndex = -1;
                let instanceIndex = -1;

                if (previousIndices.length > previousIndexCounter) {
                    studyIndex = previousIndices[previousIndexCounter].studyIndex;
                    serieIndex = previousIndices[previousIndexCounter].serieIndex;
                    instanceIndex = previousIndices[previousIndexCounter].instanceIndex;
                    previousIndexCounter++;
                }

                this.viewports[i][j] = {
                    canvas: imageCanvas,
                    overlayCanvas: overlayCanvas,
                    video: video,
                    pdf: pdf,
                    playControls: {
                        div: divPlayControls,
                        lbl: lblPlayControls,
                        btnPlay: btnPlay,
                        btnStep: btnStep,
                        btnPause: btnPause,
                        btnPlayBack: btnPlayBack,
                        btnStepBack: btnStepBack,
                        btnPauseBack: btnPauseBack,
                        btnRewind: btnRewind
                    },
                    frame: 0,
                    framecount: 1,
                    rowIndex: i,
                    colIndex: j,
                    studyIndex: studyIndex,
                    serieIndex: serieIndex,
                    instanceIndex: instanceIndex
                };

                gridItems[i][j].click(function(e) {
                    if (this.playControlsPropagation) return;

                    if (e.detail === 1) { // single click
                        let previousRow = this.currentRow;
                        let previousCol = this.currentCol;

                        this.currentRow = i;
                        this.currentCol = j;

                        if (previousRow >= 0 && previousCol >= 0) {
                            app.viewer.drawImage(this.viewports[previousRow][previousCol]);
                            app.viewer.drawThumbnail(this.viewports[previousRow][previousCol].studyIndex, this.viewports[previousRow][previousCol].serieIndex);
                        }

                        app.viewer.drawImage(app.viewer.getViewport());
                        app.viewer.drawThumbnail(app.viewer.getViewport().studyIndex, app.viewer.getViewport().serieIndex);
                    } else if (e.detail === 2) { // double click
                        let studyIndex = this.viewports[i][j].studyIndex;
                        let serieIndex = this.viewports[i][j].serieIndex;
                        let instanceIndex = this.viewports[i][j].instanceIndex;
                        if (studyIndex >= 0 && serieIndex >= 0 && instanceIndex >= 0) {
                            let tempStudyIndex = this.viewports[0][0].studyIndex;
                            let tempSerieIndex = this.viewports[0][0].serieIndex;
                            let tempInstanceIndex = this.viewports[0][0].instanceIndex;

                            this.viewports[0][0].studyIndex = studyIndex;
                            this.viewports[0][0].serieIndex = serieIndex;
                            this.viewports[0][0].instanceIndex = instanceIndex;

                            this.viewports[i][j].studyIndex = tempStudyIndex;
                            this.viewports[i][j].serieIndex = tempSerieIndex;
                            this.viewports[i][j].instanceIndex = tempInstanceIndex;

                            this.gridRows = 1;
                            this.gridCols = 1;

                            app.viewer.highlightViewports(0, 0);
                            app.viewer.makeGrid();
                        }
                    }
                });

                gridItems[i][j].on("dragover", e => e.preventDefault()); // this is necessary for on-drop to work

                gridItems[i][j].on("drop", () => {
                    if (this.draggingStudyIndex >= 0 && this.draggingSerieIndex >= 0) {
                        let previousRow = this.currentRow;
                        let previousCol = this.currentCol;

                        this.currentRow = i;
                        this.currentCol = j;

                        if (previousRow >= 0 && previousCol >= 0) {
                            this.drawImage(this.viewports[previousRow][previousCol]);
                            this.drawThumbnail(this.viewports[previousRow][previousCol].studyIndex, this.viewports[previousRow][previousCol].serieIndex);
                        }

                        this.drawImage(this.getViewport());
                        this.drawThumbnail(this.getViewport().studyIndex,this.getViewport().serieIndex);

                        this.selectSerie(this.draggingStudyIndex, this.draggingSerieIndex, true);
                    }
                });

                gridItems[i][j].on("mousedown", e => {
                    if (i !== this.currentRow || j !== this.currentCol) {
                        let previousRow = this.currentRow;
                        let previousCol = this.currentCol;

                        this.currentRow = i;
                        this.currentCol = j;

                        if (previousRow >= 0 && previousCol >= 0) {
                            this.drawImage(this.viewports[previousRow][previousCol]);
                            this.drawThumbnail(this.viewports[previousRow][previousCol].studyIndex, this.viewports[previousRow][previousCol].serieIndex);
                        }

                        this.drawImage(this.getViewport());
                        this.drawThumbnail(this.getViewport().studyIndex, this.getViewport().serieIndex);
                    }

                    let rect = e.currentTarget.getBoundingClientRect();
                    this.viewports[i][j].x = e.pageX - rect.left;
                    this.viewports[i][j].y = e.pageY - rect.top;

                    if (e.button === 0 && this.currentSubmenu === SUBMENU_MEASURES) {
                        this.measurer.startMeasure(this.viewports[i][j], false);
                    }
                });

                gridItems[i][j].on("mousemove", e => {
                    if (this.throttle) return;  // mechanism to limit the mouse move handling

                    this.throttle = true;
                    setTimeout(() => { app.viewer.throttle = false; }, 1000 / this.throttleFps);

                    if (this.leftMouseDown || this.rightMouseDown) {
                        let rect = e.currentTarget.getBoundingClientRect();
                        let deltaX = e.pageX - rect.left - this.viewports[i][j].x;
                        let deltaY = e.pageY - rect.top - this.viewports[i][j].y;

                        this.viewports[i][j].x = e.pageX - rect.left;
                        this.viewports[i][j].y = e.pageY - rect.top;

                        if (this.leftMouseDown && !this.rightMouseDown) {
                            if (this.currentSubmenu === SUBMENU_MEASURES) {
                                this.measurer.moveMeasure(this.viewports[i][j], false);
                            } else {
                                this.adjustWindowing(this.viewports[i][j], deltaX, deltaY);
                            }
                        } else if (this.rightMouseDown && !this.leftMouseDown) {
                            this.viewports[i][j].offsetx += deltaX;
                            this.viewports[i][j].offsety += deltaY;
                        }

                        this.drawImage(this.viewports[i][j]);
                    } else if (localStorage.getItem("lens")) {
                        let rect = e.currentTarget.getBoundingClientRect();

                        this.viewports[i][j].lensx = e.pageX - rect.left;
                        this.viewports[i][j].lensy = e.pageY - rect.top;
                        this.drawImage(this.viewports[i][j]);
                    }
                });

                gridItems[i][j].on("mouseup", () => {
                    if (this.leftMouseDown && this.currentSubmenu === SUBMENU_MEASURES) {
                        this.measurer.endMeasure();
                    }
                });

                gridItems[i][j].bind("mousewheel", e => {
                    let direction = e.originalEvent["wheelDelta"] > 0 ? 1 : -1;
                    let viewport = this.viewports[i][j];
                    let instance = this.getInstance(viewport);
                    if (instance == null) return;

                    let cw = (this.gridRows === 1 && this.gridCols === 1) ? this.getInnerWidth() : viewport.canvas[0].width;
                    let ch = (this.gridRows === 1 && this.gridCols === 1) ? this.getInnerHeight() : viewport.canvas[0].height;

                    if (direction > 0) {
                        instance.scale *= 1.1;
                    }
                    else {
                        instance.scale /= 1.1;

                        let trans = this.getTrans(viewport);

                        let left = trans.x;
                        let right = cw - trans.x - instance.width * instance.scale;

                        if (left > 0 && right < 0) {
                            viewport.offsetx -= Math.min(left, -right);
                        } else if (right > 0 && left < 0) {
                            viewport.offsetx += Math.min(-left, right);
                        }

                        let top = trans.y;
                        let bottom = ch - trans.y - instance.height * instance.scale;
                        if (top > 0 && bottom < 0) {
                            viewport.offsety -= Math.min(top, -bottom);
                        } else if (bottom > 0 && top < 0) {
                            viewport.offsety += Math.min(-top, bottom);
                        }
                    }

                    this.drawImage(this.viewports[i][j]);
                });

                gridItems[i][j].bind("MozMousePixelScroll", e => {
                    let direction = e.originalEvent.detail > 0 ? -1 : 1;
                    let viewport = this.viewports[i][j];
                    let instance = this.getInstance(viewport);
                    if (instance == null) return;

                    if (direction > 0) instance.scale *= 1.1;
                    else instance.scale /= 1.1;

                    this.drawImage(this.viewports[i][j]);
                });

                gridItems[i][j].on("touchstart", e => {
                    if (i !== this.currentRow || j !== this.currentCol) {
                        let previousRow = this.currentRow;
                        let previousCol = this.currentCol;

                        this.currentRow = i;
                        this.currentCol = j;

                        if (previousRow >= 0 && previousCol >= 0) {
                            this.drawImage(this.viewports[previousRow][previousCol]);
                            this.drawThumbnail(this.viewports[previousRow][previousCol].studyIndex, this.viewports[previousRow][previousCol].serieIndex);
                        }

                        this.drawImage(this.viewports[this.currentRow][this.currentCol]);
                        this.drawThumbnail(this.viewports[this.currentRow][this.currentCol].studyIndex, this.viewports[this.currentRow][this.currentCol].serieIndex);
                    }

                    let rect = e.target.getBoundingClientRect();
                    this.viewports[i][j].x = e.targetTouches[0].pageX - rect.left;
                    this.viewports[i][j].y = e.targetTouches[0].pageY - rect.top;
                    let instance = this.getInstance(this.viewports[i][j]);

                    if (e.targetTouches.length === 2) {
                        this.viewports[i][j].pinchScale = instance.scale;
                        this.viewports[i][j].pinchDistance = this.measurer.getDistance( { x: e.targetTouches[0].pageX, y: e.targetTouches[0].pageY } , { x: e.targetTouches[1].pageX, y: e.targetTouches[1].pageY } );
                    }

                    this.preventSwiping = false;
                });

                gridItems[i][j].on("touchmove", e => {
                    e.preventDefault(); // this prevents e.g. browser gestures like 'go back' on swipe

                    if (this.singleTouch || this.doubleTouch) {
                        let rect = e.target.getBoundingClientRect();
                        let deltaX = e.targetTouches[0].pageX - rect.left - this.viewports[i][j].x;
                        let deltaY = e.targetTouches[0].pageY - rect.top - this.viewports[i][j].y;

                        this.viewports[i][j].x = e.targetTouches[0].pageX - rect.left;
                        this.viewports[i][j].y = e.targetTouches[0].pageY - rect.top;

                        if (this.singleTouch) {
                            if (this.touchBlocker) {
                                if (this.currentSubmenu === SUBMENU_MEASURES)
                                    this.measurer.startMeasure(this.viewports[i][j], true);
                                if (Math.abs(deltaX) > 1 || Math.abs(deltaY) > 1) {
                                    this.touchBlocker = false;
                                }
                            }
                            if (!this.touchBlocker) {
                                if (localStorage.getItem("swiping") && !this.preventSwiping) {
                                    if (deltaX > 25) {
                                        this.preventSwiping = true;
                                        this.previous();
                                    }
                                    else if (deltaX < -25) {
                                        this.preventSwiping = true;
                                        this.next();
                                    }
                                } else if (this.currentSubmenu === SUBMENU_MEASURES) {
                                    this.measurer.moveMeasure(this.viewports[i][j], true);
                                } else {
                                    this.adjustWindowing(this.viewports[i][j], deltaX, deltaY);
                                }
                            }
                        } else if (this.doubleTouch) {
                            this.viewports[i][j].offsetx += deltaX;
                            this.viewports[i][j].offsety += deltaY;
                            let instance = this.getInstance(this.viewports[i][j]);

                            let pinchDistance = this.measurer.getDistance( { x: e.targetTouches[0].pageX, y: e.targetTouches[0].pageY } , { x: e.targetTouches[1].pageX, y: e.targetTouches[1].pageY } );
                            let scale = pinchDistance / this.viewports[i][j].pinchDistance;
                            instance.scale = this.viewports[i][j].pinchScale * scale;
                        }

                        this.drawImage(this.viewports[i][j]);
                    }
                });

                gridItems[i][j].on("touchend", () => {
                    if (this.currentSubmenu === SUBMENU_MEASURES) {
                        this.measurer.endMeasure();
                    }
                });

                divPlayControls.on("mouseover", () => {
                    this.playControlsPropagation = true;
                });

                divPlayControls.on("mouseout", () => {
                    this.playControlsPropagation = false;
                });

                divPlayControls.on("mousedown", e => {
                    e.stopPropagation();
                });
            }
        }
        this.currentRow = 0;
        this.currentCol = 0;

        for (let indices of previousIndices) {
            this.drawThumbnail(indices.studyIndex, indices.serieIndex);
        }

        this.fitImages();
    }

    fitImages() {
        for (let i = 0; i < this.gridRows; i++) {
            for (let j = 0; j < this.gridCols; j++) {
                this.fitImage(this.viewports[i][j]);
            }
        }
    }

    fitImage(viewport) {
        let studyIndex = viewport.studyIndex;
        let serieIndex = viewport.serieIndex;
        let instanceIndex = viewport.instanceIndex;

        if (studyIndex >= 0 && serieIndex >= 0 && instanceIndex >= 0) {
            let study = this.studies[studyIndex];
            let serie = study.series[serieIndex];
            let instance = serie.instances[instanceIndex];

            let canvas = viewport.canvas[0];
            let image = instance.frames[viewport.frame];
            if (image == null) image = serie.thumbnail;
            if (image == null) return;

            let cw = (this.gridRows === 1 && this.gridCols === 1) ? this.getInnerWidth() : canvas.width;
            let ch = (this.gridRows === 1 && this.gridCols === 1) ? this.getInnerHeight() : canvas.height;

            let iw = image.width;
            let ih = image.height;

            let relativeWidth = iw / cw;
            let relativeHeight = ih / ch;

            let max = Math.max(relativeWidth, relativeHeight);
            instance.scale = 1 / max;

            viewport.offsetx = (cw - iw) / 2;
            viewport.offsety = (ch - ih) / 2;

            viewport.brightness = 1;
            viewport.contrast = 1;

            viewport.fitscale = instance.scale;
            viewport.fitx = viewport.offsetx;
            viewport.fity = viewport.offsety;

            viewport.frame = 0;
            // if (serie.plane === "C" || serie.plane === "S") viewport.frame = Math.round(serie.framecount / 2);
            viewport.framecount = serie.framecount;
        }

        this.drawImage(viewport);
    }

    drawImages() {
        for (let i = 0; i < this.gridRows; i++) {
            for (let j = 0; j < this.gridCols; j++) {
                this.drawImage(this.viewports[i][j]);
            }
        }
    }

    drawImage(viewport) {
        let canvas = viewport.canvas[0];

        let gg = canvas.getContext('2d');

        gg.clearRect(0, 0, canvas.width, canvas.height);
        viewport.playControls.div.css("display", "none");

        let studyIndex = viewport.studyIndex;
        let serieIndex = viewport.serieIndex;
        let instanceIndex = viewport.instanceIndex;
        let frame = viewport.frame;
        // let plane = viewport.plane;

        if (studyIndex >= 0 && serieIndex >= 0 && instanceIndex >= 0) {
            let study = this.studies[studyIndex];
            let serie = study.series[serieIndex];
            let instance = serie.instances[instanceIndex];

            if (instance.pdf) {
                viewport.canvas.css("display", "none");
                viewport.pdf.css("display", "block");
                viewport.video.css("display", "none");

                viewport.pdf.attr("src", "html/web/viewer.html?studyId=" + study.id + "&serieId=" + serie.id + "#pagemode=none"); // open the modified PDF plugin
            } else if (instance.video) {
                viewport.canvas.css("display", "none");
                viewport.pdf.css("display", "none");
                viewport.video.css("display", "block");

                viewport.video.html("<source src='app/viewer/getVideo?studyId=" + study.id + "&serieId=" + serie.id + "' type='video/mp4'>");
            } else {
                viewport.canvas.css("display", "block");
                viewport.pdf.css("display", "none");
                viewport.video.css("display", "none");

                let image = instance.frames[frame];

                if (image == null) image = serie.thumbnail;
                if (image == null) return;

                const iw = image.width;
                const ih = image.height;
                const rotation = instance.rotation;
                const flipped = instance.flipped;
                const offsetx = viewport.offsetx;
                const offsety = viewport.offsety;

                let scalex = instance.scale;
                let scaley = instance.scale;

                if (flipped) {
                    if (rotation === 0 || rotation === 180) scalex *= -1;
                    else if (rotation === 90 || rotation === 270) scaley *= -1;
                }

                gg.save();

                gg.translate(offsetx, offsety);

                gg.translate(iw / 2, ih / 2);
                gg.scale(scalex, scaley);
                gg.translate(-iw / 2, -ih / 2);

                gg.translate(iw / 2, ih / 2);
                gg.rotate( this.measurer.toRadians(rotation) );
                gg.translate(-iw / 2, -ih / 2);


                // gg.filter = "brightness(" + (viewport.brightness * 100) + "%) contrast(" + (viewport.contrast * 100) + "%)"
                viewport.canvas.css("filter", "brightness(" + (viewport.brightness * 100) + "%) contrast(" + (viewport.contrast * 100) + "%) invert(" + (localStorage.getItem("inverted") ? 100 : 0) + "%)");

                gg.drawImage(image, 0, 0);

                gg.restore();


                if (localStorage.getItem("lens")) {
                    gg.strokeStyle = "red";

                    let lensX = viewport.lensx;
                    let lensY = viewport.lensy;

                    let trans = this.getTrans(viewport);
                    let targetX = (lensX - trans.x) / scalex;
                    let targetY = (lensY - trans.y) / scaley;

                    let lensSize = 250;
                    let lensZoom = 2;
                    let zoomSize = lensSize/lensZoom;

                    gg.drawImage(image, targetX - zoomSize/2, targetY - zoomSize/2, zoomSize, zoomSize, lensX - lensSize/2, lensY - lensSize/2, lensSize, lensSize);
                    gg.strokeRect(lensX - lensSize/2, lensY - lensSize/2, lensSize, lensSize);
                }

                // drawMeasures(viewport, instance, iw, ih, offsetx, offsety, scalex, scaley, rotation);

                if (serie.framecount > 1) {
                    viewport.playControls.div.css("display", "block");
                    // viewport.playControls.lbl.html((viewport.frame + 1) + "<br>/" + viewport.framecount);
                    viewport.playControls.lbl.html( (this.getOverallFrame(serie, instanceIndex, frame) + 1) + "<br>/" + viewport.framecount);
                }
            }
        }

        this.drawOverlay(viewport);
        this.drawMeasures(viewport); // uses the overlay canvas

        this.fillOrientations(viewport);
    }

    drawOverlay(viewport) {
        let canvas = viewport.overlayCanvas[0];
        let gg = canvas.getContext("2d");

        const cw = canvas.width;
        const ch = canvas.height;
        let color = "white";

        gg.clearRect(0, 0, canvas.width, canvas.height);

        let studyIndex = viewport.studyIndex;
        let serieIndex = viewport.serieIndex;
        let instanceIndex = viewport.instanceIndex;
        if (studyIndex >= 0 && serieIndex >= 0 && instanceIndex >= 0) {
            let study = this.studies[studyIndex];
            let serie = study.series[serieIndex];
            let instance = serie.instances[instanceIndex];
            color = study.color;

            if (localStorage.getItem("showInfo")) {
                const infoOffsetHeight = 20;
                let infoOffset;

                gg.fillStyle = color;
                gg.font = "15px Verdana";

                {
                    gg.textAlign = "left";
                    gg.textBaseline = "top";
                    infoOffset = 0;

                    if (instance.tags["CommentsOnRadiationDose"] != null && this.f6mode && instance.tags["CommentsOnRadiationDose"].split(" * ").length > 1) gg.fillText(instance.tags["CommentsOnRadiationDose"].split(" * ")[1].split("|")[1], 10, 10 + infoOffset++ * infoOffsetHeight);
                    if (instance.tags["PatientName"] !== undefined) gg.fillText(instance.tags["PatientName"], 10, 10 + infoOffset++ * infoOffsetHeight);
                    if (instance.tags["StudyComments"] !== undefined) gg.fillText(instance.tags["StudyComments"], 10, 10 + infoOffset++ * infoOffsetHeight);
                    if (instance.tags["PatientId"] !== undefined) gg.fillText(instance.tags["PatientId"], 10, 10 + infoOffset++ * infoOffsetHeight);
                    if (instance.tags["PatientComments"] !== undefined) gg.fillText(instance.tags["PatientComments"], 10, 10 + infoOffset++ * infoOffsetHeight);
                    if (instance.tags["PatientSex"] !== undefined) gg.fillText(instance.tags["PatientSex"], 10, 10 + infoOffset++ * infoOffsetHeight);
                    if (instance.tags["PatientBirthDate"] !== undefined) gg.fillText(instance.tags["PatientBirthDate"], 10, 10 + infoOffset++ * infoOffsetHeight);
                    if (instance.tags["OtherId"] !== undefined) gg.fillText(instance.tags["OtherId"], 10, 10 + infoOffset++ * infoOffsetHeight);
                    if (instance.tags["BreedRegistrationNumber"] !== undefined) gg.fillText(instance.tags["BreedRegistrationNumber"], 10, 10 + infoOffset++ * infoOffsetHeight);
                }

                {
                    gg.textAlign = "right";
                    gg.textBaseline = "top";
                    infoOffset = 0;
                    if (instance.tags["CommentsOnRadiationDose"] != null && this.f6mode && instance.tags["CommentsOnRadiationDose"].split(" * ").length > 1) gg.fillText(instance.tags["CommentsOnRadiationDose"].split(" * ")[1].split("|")[2], cw - 10, 10 + infoOffset++ * infoOffsetHeight);
                    if (instance.tags["InstitutionName"] !== undefined) gg.fillText(instance.tags["InstitutionName"], cw - 10, 10 + infoOffset++ * infoOffsetHeight);
                    let timestamp = this.formatDate(instance.tags["StudyDate"]) + "  " + this.formatTime(instance.tags["SeriesTime"]);
                    gg.fillText(timestamp, cw - 10, 10 + infoOffset++ * infoOffsetHeight);
                    if (serie.mainDicomTags["SeriesDescription"] !== undefined) gg.fillText(serie.mainDicomTags["SeriesDescription"], cw - 10, 10 + infoOffset++ * infoOffsetHeight);
                }

                {
                    gg.textAlign = "left";
                    gg.textBaseline = "bottom";
                    infoOffset = 0;
                    if (instance.tags["CommentsOnRadiationDose"] != null && this.f6mode && instance.tags["CommentsOnRadiationDose"].split(" * ").length > 1) gg.fillText(instance.tags["CommentsOnRadiationDose"].split(" * ")[1].split("|")[0], 10, ch - 10 - infoOffset++ * infoOffsetHeight);
                    if (instance.tags["CommentsOnRadiationDose"] != null && this.f6mode) {
                        gg.fillText(instance.tags["CommentsOnRadiationDose"].split(" * ")[0], 10, ch - 10 - infoOffset++ * infoOffsetHeight);
                    }
                    if (serie.mainDicomTags["SeriesNumber"] !== undefined) gg.fillText(serie.mainDicomTags["SeriesNumber"], 10, ch - 10 - infoOffset++ * infoOffsetHeight);
                    // if (instance.tags.CommentsOnRadiationDose !== undefined) gg.fillText(instance.tags.CommentsOnRadiationDose, 10, ch - 10 - infoOffset++ * infoOffsetHeight);
                }

                {
                    gg.textAlign = "right";
                    gg.textBaseline = "bottom";
                    infoOffset = 0;
                    if (instance.tags["CommentsOnRadiationDose"] != null && this.f6mode && instance.tags["CommentsOnRadiationDose"].split(" * ").length > 1) gg.fillText(instance.tags["CommentsOnRadiationDose"].split(" * ")[1].split("|")[3], cw - 10, ch - 10 - infoOffset++ * infoOffsetHeight);
                    if (instance.tags["Manufacturer"] !== "") gg.fillText(instance.tags["Manufacturer"], cw - 10, ch - 10 - infoOffset++ * infoOffsetHeight);
                }

            }
        } else {
            gg.font = "15px Verdana";
            gg.fillStyle = "white";
            gg.textBaseline = "middle";
            gg.textAlign = "center";

            let text = this.loading ? "%{Loading}" : "%{Drag_a_series_here}";
            gg.fillText(text, cw/2, ch/2);
        }

        if (this.gridRows > 1 || this.gridCols > 1) {
            if (currentRow === viewport.rowIndex && currentCol === viewport.colIndex) {
                gg.strokeStyle = color;
                gg.setLineDash([]);
                gg.lineWidth = 5;
                gg.strokeRect(0, 0, cw, ch);
            } else if (studyIndex >= 0 && serieIndex >= 0) {
                gg.strokeStyle = color;
                gg.setLineDash([5,5]);
                gg.lineWidth = 5;
                gg.strokeRect(0, 0, cw, ch);
            }
        }
    }

    adjustWindowing(viewport, deltaX, deltaY) {
        if (deltaX < 0) viewport.contrast *= (1 - deltaX/1000);
        else if (deltaX > 0) viewport.contrast /= (1 + deltaX/1000);

        if (deltaY < 0) viewport.brightness *= (1 - deltaY/1000);
        else if (deltaY > 0) viewport.brightness /= (1 + deltaY/1000);
    }

    getTrans(viewport) {
        let cw = viewport.overlayCanvas.width();
        let ch = viewport.overlayCanvas.height();

        let instance = this.getInstance(this.getViewport());
        if (instance == null) return;


        let iw = instance.width;
        let ih = instance.height;

        let x = (cw - (iw * instance.scale)) / 2 + (viewport.offsetx - viewport.fitx);
        let y = (ch - (ih * instance.scale)) / 2 + (viewport.offsety - viewport.fity);

        return { x:x, y: y};
    }

    isActive(studyIndex, serieIndex) {
        return this.getViewport().studyIndex === studyIndex && this.getViewport().serieIndex === serieIndex;
    }

    isShown(studyIndex, serieIndex) {
        for (let i = 0; i < this.gridRows; i++) {
            for (let j = 0; j < this.gridCols; j++) {
                let viewport = this.viewports[i][j];
                if (viewport.studyIndex === studyIndex && viewport.serieIndex === serieIndex) {
                    // if (studies[studyIndex].series[serieIndex].instances[instanceIndex].currentframe === frame)
                    return true;
                }
            }
        }
        return false;
    }

    getShownIndices() {
        let result = [];

        for (let i = 0; i < this.viewports.length; i++) {
            for (let j = 0; j < this.viewports[i].length; j++) {
                let viewport = this.viewports[i][j];
                if (viewport.studyIndex > -1 && viewport.serieIndex > -1) {
                    result.push({
                        studyIndex: viewport.studyIndex,
                        serieIndex: viewport.serieIndex,
                        instanceIndex: viewport.instanceIndex
                    });
                }
            }
        }

        return result;
    }

    clearViewport() {
        let viewport = this.getViewport();

        let studyIndex = viewport.studyIndex;
        let serieIndex = viewport.serieIndex;
        let instanceIndex = viewport.instanceIndex;

        if (studyIndex >= 0 && serieIndex >= 0 && instanceIndex >= 0) {
            viewport.studyIndex = -1;
            viewport.serieIndex = -1;
            viewport.instanceIndex = -1;

            this.drawThumbnail(studyIndex, serieIndex);
            this.drawImage(viewport);
        }

        this.highlightSplitAndDownload();
    }

    drawMeasures(viewport) {
        let canvas = viewport.overlayCanvas[0];
        let gg = canvas.getContext("2d");

        let studyIndex = viewport.studyIndex;
        let serieIndex = viewport.serieIndex;
        let instanceIndex = viewport.instanceIndex;
        let frame = viewport.frame;

        if (studyIndex >= 0 && serieIndex >= 0 && instanceIndex >= 0) {
            let study = this.studies[studyIndex];
            let serie = study.series[serieIndex];
            let instance = serie.instances[instanceIndex];

            let image = instance.frames[frame];
            if (image == null) return;

            const iw = image.width;
            const ih = image.height;
            const rotation = instance.rotation;
            const flipped = instance.flipped;
            const offsetx = viewport.offsetx;
            const offsety = viewport.offsety;

            let scalex = instance.scale;
            let scaley = instance.scale;

            if (flipped) {
                if (rotation === 0 || rotation === 180) scalex *= -1;
                else if (rotation === 90 || rotation === 270) scaley *= -1;
            }

            let found = false;
            for (let shape of instance.shapes) {
                if (!shape.deleted) {
                    found = true;

                    let zoomx = scalex;
                    let zoomy = scaley;

                    if (shape.flipped) {
                        if (rotation === 0 || rotation === 180) zoomx *= -1;
                        else if (rotation === 90 || rotation === 270) zoomy *= -1;
                    }

                    gg.save();

                    gg.translate(offsetx, offsety);

                    gg.translate(iw / 2, ih / 2);
                    gg.scale(zoomx, zoomy);
                    gg.translate(-iw / 2, -ih / 2);

                    gg.translate(iw / 2, ih / 2);
                    gg.rotate( this.measurer.toRadians(rotation - shape.rotation) );
                    gg.translate(-iw / 2, -ih / 2);

                    shape.draw(gg);

                    gg.restore();
                }
            }

            $("#btnMeasureSelectAll").attr("disabled", !found);
            $("#btnMeasureDelete").attr("disabled", !found);

            found = false;
            for (let study of this.studies) {
                for (let serie of study.series) {
                    for (let instance of serie.instances) {
                        for (let shape of instance.shapes) {
                            if (!shape.deleted) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
            }

            $("#btnMeasureSave").attr("disabled", !found);
        }
    }

    getOverallFrame(serie, instanceIndex, frame) {
        let result = 0;
        for (let i = 0; i < serie.instances.length; i++) {
            if (i === instanceIndex) {
                result += frame;
                return result;
            } else {
                result += serie.instances[i].framecount;
            }
        }
        return -1;
    }

    getInstanceAndFrame(serie, overallFrame) {
        let temp = 0;

        for (let i = 0; i < serie.instances.length; i++) {
            if (overallFrame < temp + serie.instances[i].framecount) {
                return {
                    instanceIndex: i,
                    frame: overallFrame - temp
                };
            } else {
                temp += serie.instances[i].framecount;
            }
        }
    }

    step(row, col, offset) {
        let viewport = this.viewports[row][col];
        if (viewport.framecount < 2) return;

        let serie = this.studies[viewport.studyIndex].series[viewport.serieIndex];

        let overallFrame = this.getOverallFrame(serie, viewport.instanceIndex, viewport.frame);
        overallFrame = (overallFrame + viewport.framecount + offset) % viewport.framecount;
        let instanceAndFrame = this.getInstanceAndFrame(serie, overallFrame);

        viewport.instanceIndex = instanceAndFrame.instanceIndex;
        viewport.frame = instanceAndFrame.frame;

        // drawImage(viewport);
        this.fitImage(viewport);
    }

    play(row, col, forward = true) {
        let viewport = this.viewports[row][col];
        if (viewport.framecount < 2) return;

        if (viewport.playing) {
            this.stop(row, col);
            return;
        }
        viewport.playing = true;

        viewport.playControls.btnRewind.attr("disabled", true);
        viewport.playControls.btnPlayBack.attr("disabled", true);
        viewport.playControls.btnStepBack.attr("disabled", true);
        viewport.playControls.btnStep.attr("disabled", true);
        viewport.playControls.btnPlay.attr("disabled", true);

        if (forward) {
            viewport.playControls.btnPlay.css("display", "none");
            viewport.playControls.btnPause.css("display", "inline");
        } else {
            viewport.playControls.btnPlayBack.css("display", "none");
            viewport.playControls.btnPauseBack.css("display", "inline");
        }

        clearInterval(viewport.playControls.timer);
        viewport.playControls.timer = setInterval( () => this.step(row, col, forward ? +1 : -1), 1000 / this.fps );
    }

    stop(row, col) {
        let viewport = this.viewports[row][col];
        if (viewport.framecount < 2) return;

        viewport.playing = false;

        viewport.playControls.btnRewind.attr("disabled", false);
        viewport.playControls.btnPlayBack.attr("disabled", false);
        viewport.playControls.btnStepBack.attr("disabled", false);
        viewport.playControls.btnStep.attr("disabled", false);
        viewport.playControls.btnPlay.attr("disabled", false);

        viewport.playControls.btnPlay.css("display", "inline");
        viewport.playControls.btnPlayBack.css("display", "inline");

        viewport.playControls.btnPause.css("display", "none");
        viewport.playControls.btnPauseBack.css("display", "none");

        clearInterval(viewport.playControls.timer);
    }

    rewind(row, col) {
        let viewport = this.viewports[row][col];
        if (viewport.framecount < 2) return;

        viewport.instanceIndex = 0;
        viewport.frame = 0;

        this.fitImage(viewport);
    }

    // tools

    contains(rect, x, y) {
        return x >= rect.x && x <= rect.x + rect.width && y >= rect.y && y <= rect.y + rect.height;
    }

    formatDate(strDate) {
        if (strDate == null) {
            return "";
        } else if (strDate.length === 8) {
            return strDate.substr(0, 4) + "-" + strDate.substr(4, 2) + "-" + strDate.substr(6, 2);
        } else if (strDate.length === 6) {
            return strDate.substr(0, 2) + "-" + strDate.substr(2, 2) + "-" + strDate.substr(4, 2);
        } else {
            return strDate;
        }
    }

    formatTime(strTime) {
        if (strTime == null) {
            return "";
        } else if (strTime.length === 6) {
            return strTime.substr(0, 2) + ":" + strTime.substr(2, 2) + ":" + strTime.substr(4,2);
        } else if (strTime.length === 4) {
            return strTime.substr(0, 2) + ":" + strTime.substr(2, 2) + ":00";
        } else {
            return strTime;
        }
    }

}


































