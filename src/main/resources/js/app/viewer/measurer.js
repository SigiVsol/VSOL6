// const btnMeasureBack = $("#btnMeasureBack"), btnMeasureSelectAll = $("#btnMeasureSelectAll"), btnMeasureDelete = $("#btnMeasureDelete"), btnMeasureSettings = $("#btnMeasureSettings"), btnMeasureSave = $("#btnMeasureSave");
// const btnLines = $("#btnLines"), btnCircles = $("#btnCircles"), btnDots = $("#btnDots"), btnAngles = $("#btnAngles");
// const btnCobb = $("#btnCobb"), btnParallels = $("#btnParallels"), btnPerpendiculars = $("#btnPerpendiculars"), btnCalibration = $("#btnCalibration");
// const btnVHS = $("#btnVHS"), btnCTR = $("#btnCTR"), btnTPA = $("#btnTPA"), btnTTA = $("#btnTTA"), btnDI = $("#btnDI"), btnVEZ = $("#btnVEZ"), btnNOA = $("#btnNOA");
// const btnCalibrate1 = $("#btnCalibrate1"), btnCalibrate2 = $("#btnCalibrate2"), btnCalibrate5 = $("#btnCalibrate5"), btnCalibrateCustom = $("#btnCalibrateCustom"), btnCalibrateReset = $("#btnCalibrateReset");
// const radMeasure = $(".radMeasure");
//
// const lblAngles1 = $("#lblAngles1"), lblAngles2 = $("#lblAngles2");
// const lblCobb1 = $("#lblCobb1"), lblCobb2 = $("#lblCobb2");
// const lblParallels1 = $("#lblParallels1"), lblParallels2 = $("#lblParallels2");
// const lblPerpendiculars1 = $("#lblPerpendiculars1"), lblPerpendiculars2 = $("#lblPerpendiculars2");
// const lblCalibration1 = $("#lblCalibration1"), lblCalibration2 = $("#lblCalibration2");
// const lblVHS1 = $("#lblVHS1"), lblVHS2 = $("#lblVHS2"), lblVHS3 = $("#lblVHS3");
// const lblCTR1 = $("#lblCTR1"), lblCTR2 = $("#lblCTR2"), lblCTR3 = $("#lblCTR3"), lblCTR4 = $("#lblCTR4");
// const lblTPA1 = $("#lblTPA1"), lblTPA2 = $("#lblTPA2"), lblTPA3 = $("#lblTPA3");
// const lblTTA1 = $("#lblTTA1"), lblTTA2 = $("#lblTTA2"), lblTTA3 = $("#lblTTA3");
// const lblDI1 = $("#lblDI1"), lblDI2 = $("#lblDI2");
// const lblVEZ1 = $("#lblVEZ1"), lblVEZ2 = $("#lblVEZ2");
// const lblNOA1 = $("#lblNOA1"), lblNOA2 = $("#lblNOA2"), lblNOA3 = $("#lblNOA3"), lblNOA4 = $("#lblNOA4");


const MEASURE_NONE = 0, MEASURE_LINES = 1, MEASURE_CIRCLES = 2, MEASURE_DOTS = 3, MEASURE_ANGLES = 4, MEASURE_COBB = 5, MEASURE_PARALLELS = 6, MEASURE_PERPENDICULARS = 7, MEASURE_CALIBRATION = 8, MEASURE_VHS = 9, MEASURE_CTR = 10, MEASURE_TPA = 11, MEASURE_TTA = 12, MEASURE_DI = 13, MEASURE_VEZ = 14, MEASURE_NOA = 15, MEASURE_SETTINGS = 16;

const fontHeight = 18, mouseRange = 10, touchRange = 20;
const RAD180 = Math.PI, RAD360 = 2 * Math.PI;
const charSquare = String.fromCharCode(0x00B2);
const charDegree = String.fromCharCode(0x00B0);

class Measurer {
    
    constructor() {
        this.calibrationMultiplier = 1.0;
        this.currentMeasure = MEASURE_NONE;
        this.selectionShape = null;
        this.selectionPoint = null;
        this.exporting = false;
        this.divsMeasures = [ $("#divMeasureNone"), $("#divMeasureLines"), $("#divMeasureCircles"), $("#divMeasureDots"), $("#divMeasureAngles"), $("#divMeasureCobb"), $("#divMeasureParallels"), $("#divMeasurePerpendiculars"), $("#divMeasureCalibration"), $("#divMeasureVHS"), $("#divMeasureCTR"), $("#divMeasureTPA"), $("#divMeasureTTA"), $("#divMeasureDI"), $("#divMeasureVEZ"), $("#divMeasureNOA"), $("#divMeasureSettings") ];
    }

    setMeasure(measure) {
        const btnMeasureBack = $("#btnMeasureBack");
        this.currentMeasure = measure;

        for (let div of this.divsMeasures) div.css("display", "none");
        this.divsMeasures[measure].css("display", "block");

        if (measure === MEASURE_NONE) {
            btnMeasureBack.attr("disabled", true);

            if (this.selectionShape != null && this.selectionShape instanceof Calibration) {
                this.selectionShape.deleted = true;
                this.selectionShape = null;
                app.viewer.drawImage(app.viewer.getViewport());
            }
        } else {
            btnMeasureBack.attr("disabled", false);
        }

        let height = $("#divMeasures").css("height");
        $("#menuBarTop").css("height", "calc(100% - " + height + ")");
    }

    startMeasure(viewport, touch = false) {
        let instance = app.viewer.getInstance(viewport);
        if (instance == null) return;

        let trans = app.viewer.getTrans(viewport);
        let x = (viewport.x - trans.x) / instance.scale;
        let y = (viewport.y - trans.y) / instance.scale;

        this.selectionPoint = null;

        for (let shape of instance.shapes) {
            if (shape.deleted) continue;

            shape.selected = false;
            let point = shape.attemptSelection(x, y, touch);
            if (point != null) {
                this.selectionShape = shape;
                this.selectionPoint = point;
                this.selectionShape.selected = true;
            }
        }

        if (this.selectionPoint == null) {
            if (this.selectionShape == null) { // start making a new drawing in the selected shape
                if (this.currentMeasure > MEASURE_NONE && this.currentMeasure !== MEASURE_SETTINGS) {
                    this.selectionPoint = new SelectionPoint(instance, x, y);
                    switch (this.currentMeasure) {
                        case MEASURE_LINES: this.selectionShape = new Line(this.selectionPoint); break;
                        case MEASURE_CIRCLES: this.selectionShape = new Circle(this.selectionPoint); break;
                        case MEASURE_DOTS: this.selectionShape = new Dot(this.selectionPoint); break;
                        case MEASURE_ANGLES: this.selectionShape = new Angle(this.selectionPoint); break;
                        case MEASURE_COBB: this.selectionShape = new Cobb(this.selectionPoint); break;
                        case MEASURE_VHS: this.selectionShape = new VHS(this.selectionPoint); break;
                        case MEASURE_CTR: this.selectionShape = new CTR(this.selectionPoint); break;
                        case MEASURE_NOA: this.selectionShape = new NOA(this.selectionPoint); break;
                        case MEASURE_TPA: this.selectionShape = new TPA(this.selectionPoint); break;
                        case MEASURE_DI: this.selectionShape = new DI(this.selectionPoint); break;
                        case MEASURE_TTA: this.selectionShape = new TTA(this.selectionPoint); break;
                        case MEASURE_VEZ: this.selectionShape = new Vezzoni(this.selectionPoint); break;
                        case MEASURE_PARALLELS: this.selectionShape = new Parallel(this.selectionPoint); break;
                        case MEASURE_PERPENDICULARS: this.selectionShape = new Perpendicular(this.selectionPoint); break;
                        case MEASURE_CALIBRATION: this.selectionShape = new Calibration(this.selectionPoint); break;
                    }
                    instance.shapes.push( this.selectionShape );
                }
            } else { // continue the selected multi-step shape (via move measure)
                this.selectionPoint = new SelectionPoint(instance, x, y);
                this.selectionShape.selected = true;
                this.selectionShape.next(this.selectionPoint);
            }
        }

        app.viewer.drawImage(viewport);
    }

    moveMeasure(viewport, touch = false) {
        let instance = app.viewer.getInstance(viewport);
        if (instance == null) return;

        let trans = app.viewer.getTrans(viewport);
        let x = (viewport.x - trans.x) / instance.scale;
        let y = (viewport.y - trans.y) / instance.scale;

        if (this.selectionShape != null) {
            if (touch)
                this.selectionShape.move(this.selectionPoint, x - 100, y);
            else
                this.selectionShape.move(this.selectionPoint, x, y);
            app.viewer.drawImage(viewport);
        }
    }

    endMeasure() {
        if (this.selectionShape != null) {
            this.selectionShape.updateLabels();

            if (this.selectionShape.isComplete()) {
                this.selectionShape = null;
            }
        }
    }

    saveMeasure() {
        let overlays = [];

        for (let study of app.viewer.studies) {
            for (let serie of study.series) {
                for (let instance of serie.instances) {
                    let shapes = [];

                    for (let shape of instance.shapes) {
                        if (!shape.deleted) {
                            console.log("SHAPE " + shape);
                            shapes.push(shape);
                        }
                    }

                    if (shapes.length === 0) continue;

                    let canvas = document.createElement("canvas");
                    let gg = canvas.getContext("2d");
                    canvas.width = instance.width;
                    canvas.height = instance.height;

                    // if (instance.inverted) {
                    //     gg.filter = "invert(100%)"; // so the overlay is burned in inverted (so normal when inverted back)
                    // }

                    this.exporting = true;
                    for (let shape of shapes) {
                        gg.save();

                        let rotation = 0;
                        let zoomx = 1;
                        let zoomy = 1;

                        if (shape.flipped) {
                            if (rotation === 0 || rotation === 180) zoomx *= -1;
                            else if (rotation === 90 || rotation === 270) zoomy *= -1;
                        }

                        gg.translate(instance.width / 2, instance.height / 2);
                        gg.scale(zoomx, zoomy);
                        gg.translate(-instance.width / 2, -instance.height / 2);

                        gg.translate(instance.width / 2, instance.height / 2);
                        gg.rotate( this.toRadians(- shape.rotation) );
                        gg.translate(-instance.width / 2, -instance.height / 2);

                        shape.draw(gg, true);

                        gg.restore();
                    }
                    this.exporting = false;

                    let dataUrl = canvas.toDataURL("image/png").substr("data:image/png;base64,".length);

                    overlays.push({
                        vsol4Id: study.vsol4Id,
                        studyId: study.id,
                        serieId: serie.id,
                        instanceId: instance.id,
                        dataUrl: dataUrl
                    });
                }
            }
        }

        if (overlays.length > 0) {
            Dialog.getString("Enter a study description.", "Measurements", description => {

                for (let overlay of overlays) {
                    overlay.description = description;
                }

                // TODO ? uid ?
                // $.post("api/request/overlay?uid=" + uid, JSON.stringify(overlays), json => {
                //     app.viewer.addStudies(json);
                //     app.viewer.getSeries(app.viewer.studies[app.viewer.studies.length - 1]);
                // }, "json");

            });
        }
    }

    // Helper functions

    drawSelectionPoint(gg, point) {
        if (this.exporting) return;
        if (point == null) return;

        // const viewport = getViewport();
        const instance = point.instance;

        gg.lineWidth = 1.5 / instance.scale;
        gg.strokeStyle = instance.serie.study.color;

        if (!(point instanceof TextPoint)) {
            gg.setLineDash([]);
            gg.beginPath();
            gg.arc(point.x, point.y, mouseRange / instance.scale, 0, 2 * Math.PI);
            gg.stroke();
        }

        if ((point instanceof TextPoint) && point.text !== "") {
            gg.setLineDash([]);
            let rect = point.getRect();
            gg.strokeRect(rect.x, rect.y, rect.width, rect.height);
        }
    }

    drawMarker(gg, point, cross = false) {
        gg.fillStyle = "white";
        gg.strokeStyle = "white";
        const instance = point.instance;
        const x = point.x, y = point.y;

        let radius = 2.5 / instance.scale;
        gg.lineWidth = 1.5 / instance.scale;

        gg.beginPath();
        gg.moveTo(x - radius, y);
        gg.lineTo(x + radius, y);
        gg.moveTo(x, y - radius);
        gg.lineTo(x, y + radius);
        gg.stroke();
    }

    drawTextPoint(gg, textpoint) {
        // const viewport = getViewport();

        let instance = textpoint.instance;
        if (instance == null) return;

        let width;
        let height = fontHeight / instance.scale;

        let extraWidth = (mouseRange / 2) / instance.scale;
        let extraHeight = mouseRange / instance.scale;

        let canvas = document.createElement("canvas");
        let ff = canvas.getContext("2d");
        ff.font = height + "px Verdana";
        width = ff.measureText(textpoint.text).width;

        canvas.width = width + extraWidth;
        canvas.height = height + extraHeight;

        let scalex = 1, scaley = 1;
        let rotation = instance.rotation;
        let flipped = instance.flipped ^ textpoint.shape.flipped;
        if (flipped) {
            if (rotation === 0 || rotation === 180) scalex *= -1;
            else if (rotation === 90 || rotation === 270) scaley *= -1;
        }

        ff.textAlign = "left";
        ff.textBaseline = "top";
        ff.fillStyle = "white";
        ff.font = height + "px Verdana";

        ff.translate(width / 2, height / 2);
        ff.scale(scalex, scaley);
        ff.translate(-width / 2, -height / 2);

        ff.fillText(textpoint.text, flipped ? -extraWidth / 2 : extraWidth / 2, extraHeight / 2);

        gg.drawImage(canvas, textpoint.x, textpoint.y - (height / 2));
    }

    drawLine(gg, start, end, dashed = false, style = "white", width = 2.0) {
        if (start == null || end == null) return;
        let instance = start.instance;
        if (instance == null) instance = end.instance;

        let segment = (width * 2) / instance.scale;

        gg.lineWidth = width / instance.scale;
        gg.strokeStyle = style;

        if (dashed) gg.setLineDash([segment, segment]);
        else gg.setLineDash([]);

        gg.beginPath();
        gg.moveTo(start.x, start.y);
        gg.lineTo(end.x, end.y);
        gg.stroke();
    }

    drawCircle(gg, center, radius, dashed = false, style = "white", width = 2.0) {
        if (center == null) return;
        const instance = center.instance;
        let segment = (width * 2) / instance.scale;

        gg.lineWidth = width / instance.scale;
        gg.strokeStyle = style;

        if (dashed) gg.setLineDash([segment, segment]);
        else gg.setLineDash([]);

        gg.beginPath();
        gg.arc(center.x, center.y, radius, 0, Math.PI * 2);
        gg.stroke();
    }

    drawCircleFromPoints(gg, a, b, c, instance = null) {
        let middle1 = this.getHalfwayPoint(a, b);
        let middle2 = this.getHalfwayPoint(b, c);

        let slope = -1 / this.getSlope(a, b); // this ensures perpendicularity
        let x2 = middle1.x + 100;
        let y2 = slope * x2 - slope * middle1.x + middle1.y;
        let tempPoint1 = { x: x2, y: y2, instance: instance }; // arbitrary point, to make a line, using the calculated slope

        slope = -1 / this.getSlope(b, c); // this ensures perpendicularity
        x2 = middle2.x + 100;
        y2 = slope * x2 - slope * middle2.x + middle2.y;
        let tempPoint2 = {x: x2, y: y2};

        let center = this.getLineIntersection(middle1, tempPoint1, middle2, tempPoint2, instance);
        let radius = this.getDistance(center, a); // or b or c, doesn't matter

        this.drawCircle(gg, center, radius);

        return center;
    }

    drawInnerAngleArc(gg, center, startAngle, endAngle, anticlockwise, fillStyle = "white") {
        if (center == null) return;
        const instance = center.instance;
        let radius = 20 / instance.scale;

        gg.fillStyle = fillStyle;
        gg.setLineDash([]);

        gg.beginPath();
        gg.arc(center.x, center.y, radius, startAngle, endAngle, anticlockwise);
        gg.lineTo(center.x, center.y);
        gg.closePath();
        gg.stroke();
        // gg.fill();
    }

    drawOuterAngleArc(gg, center, startAngle, endAngle, anticlockwise, strokeStyle = "white") {
        if (center == null) return;
        const instance = center.instance;
        let radius = 20 / instance.scale;
        let segment = 4 / instance.scale;

        gg.strokeStyle = strokeStyle;
        gg.setLineDash([segment, segment]);

        gg.beginPath();
        gg.arc(center.x, center.y, radius, startAngle, endAngle, anticlockwise);
        gg.stroke();
    }

    getSlope(start, end) { return (end.y - start.y) / (end.x - start.x); }

    getAngleXAxis(start, end) { return Math.atan2((end.y - start.y), (end.x - start.x)); }

    getAngleYAxis(start, end) { return Math.atan((end.y - start.y) / (end.x - start.x)); }

    getAcuteAngle(start1, end1, start2, end2) {
        let a1 = this.getSlope(start1, end1);
        let a2 = this.getSlope(start2, end2);

        if (!Number.isFinite(a1) && !Number.isFinite(a2)) { // line1 = line2 = y-axis
            return 0;
        } else if (!Number.isFinite(a1)) { // line1 = y-axis
            return this.getAngleYAxis(start2, end2);
        } else if (!Number.isFinite(a2)) { // line2 = y-axis
            return this.getAngleYAxis(start1, end1);
        } else {
            let tangent = (a2 - a1) / (1 + a1 * a2);
            return Math.abs(Math.atan(tangent));
        }
    }

    getLineIntersection(start1, end1, start2, end2, instance = null) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) return;

        const a1 = this.getSlope(start1, end1);
        const a2 = this.getSlope(start2, end2);

        if (a1 === a2) return null; // parallel lines

        if (!Number.isFinite(a1)) {
            return { x: start1.x, y: start2.y, instance: instance };
        } else if (!Number.isFinite(a2)) {
            return { x: start2.x, y: start1.y, instance: instance }
        } else {
            const b1 = start1.y - a1 * start1.x;
            const b2 = start2.y - a2 * start2.x;

            const x = (b2 - b1) / (a1 - a2);
            const y = (a1 * x) + b1;

            return { x: x, y: y, instance: instance };
        }
    }

    getPointIntersection(start, end, point, instance = null) {
        const a0 = (end.y - start.y) / (end.x - start.x);
        const a1 = -1 / a0;

        const b1 = point.y - a1 * point.x;

        let x1, y1;

        if (!Number.isFinite(a1)) {
            x1 = point.x;
            y1 = point.y + 100.0;
        } else if (a1 === 0.0) {
            x1 = point.x + 100.0;
            y1 = point.y;
        } else {
            y1 = 0.0;
            x1 = (y1 - b1) / a1;
        }

        return this.getLineIntersection( start, end, point, { x: x1, y: y1, instance: instance } );
    }

    getHalfwayPoint(a, b) {
        let x = (a.x + b.x) / 2;
        let y = (a.y + b.y) / 2;

        return {x: x, y: y};
    }

    getMetricSurface(squarePixels, fractionDigits = 2) {
        let instance = app.viewer.getInstance(app.viewer.getViewport());
        if (instance == null) return;

        let value = squarePixels * Math.pow(instance.pixelspacing * this.calibrationMultiplier, 2)
        let unit = "mm";

        if (value > 100) {
            value /= 100;
            unit = "cm";
        }
        if (value > 10000) {
            value /= 10000;
            unit = "m";
        }

        return Double.toString(value, fractionDigits) + " " + unit + charSquare;
    }

    getImperialSurface(squarePixels, fractionDigits = 2) {
        let instance = app.viewer.getInstance(app.viewer.getViewport());
        if (instance == null) return;

        let value = squarePixels * Math.pow(instance.pixelspacing * this.calibrationMultiplier, 2)
        let unit = "in";
        value = value / 25.4 / 25.4

        return Double.toString(value, fractionDigits) + " " + unit + charSquare;
    }

    getMetricDistance(pixels, fractionDigits = 2) {
        const viewport = app.viewer.getViewport();
        let instance = app.viewer.getInstance(viewport);
        if (instance == null) return;

        let value = pixels * instance.pixelspacing * this.calibrationMultiplier;
        let unit = "mm";

        if (value > 10) {
            value /= 10;
            unit = "cm";
        }
        if (value > 100) {
            value /= 100;
            unit = "m";
        }

        return Double.toString(value, fractionDigits) + " " + unit;
    }

    getImperialDistance(pixels, fractionDigits = 2) {
        const viewport = app.viewer.getViewport();

        let value = pixels * app.viewer.getInstance(viewport).pixelspacing * this.calibrationMultiplier;
        value /= 25.4;
        let unit = "in";

        return Double.toString(value, fractionDigits) + " " + unit;
    }

    get5PointCircleScore(a, b, c, d, e) {
        let middle1 = this.getHalfwayPoint(a, b);
        let middle2 = this.getHalfwayPoint(b, c);

        let slope = -1 / this.getSlope(a, b); // this ensures perpendicularity
        let x2 = middle1.x + 100;
        let y2 = slope * x2 - slope * middle1.x + middle1.y;
        let tempPoint1 = {x: x2, y: y2}; // arbitrary point, to make a line, using the calculated slope

        slope = -1 / this.getSlope(b, c); // this ensures perpendicularity
        x2 = middle2.x + 100;
        y2 = slope * x2 - slope * middle2.x + middle2.y;
        let tempPoint2 = {x: x2, y: y2};

        let center = this.getLineIntersection(middle1, tempPoint1, middle2, tempPoint2);
        let radius = this.getDistance(center, a); // or b or c, doesn't matter

        let scoreD = Math.abs(radius - this.getDistance(center, d));
        let scoreE = Math.abs(radius - this.getDistance(center, e));

        return (scoreD + scoreE) / 2;
    }

    toRadians(degrees) {
        return degrees * (Math.PI / 180);
    }

    toDegrees(radians) {
        return (180 * radians) / Math.PI;
    }

    getDistance(a, b) {
        return Math.hypot(a.x - b.x, a.y - b.y);
    }

    get4PointCircleScore(a, b, c, d) {
        let middle1 = this.getHalfwayPoint(a, b);
        let middle2 = this.getHalfwayPoint(b, c);

        let slope = -1 / this.getSlope(a, b); // this ensures perpendicularity
        let x2 = middle1.x + 100;
        let y2 = slope * x2 - slope * middle1.x + middle1.y;
        let tempPoint1 = {x: x2, y: y2}; // arbitrary point, to make a line, using the calculated slope

        slope = -1 / this.getSlope(b, c); // this ensures perpendicularity
        x2 = middle2.x + 100;
        y2 = slope * x2 - slope * middle2.x + middle2.y;
        let tempPoint2 = {x: x2, y: y2};

        let center = this.getLineIntersection(middle1, tempPoint1, middle2, tempPoint2);
        let radius = this.getDistance(center, a); // or b or c, doesn't matter

        return Math.abs(radius - this.getDistance(center, d));
    }

    calibrate(cm) {
        let instance = app.viewer.getInstance(app.viewer.getViewport());
        if (instance == null) return;

        if (cm == null) {
            Dialog.getString("%{Enter_calibration_cm.}", "0", this.calibrate);
            return;
        }

        cm = Number(cm);

        if (isNaN(cm) || cm <= 0) {
            alert("%{Invalid_value.}");
            return;
        }

        if (this.selectionShape == null || !(this.selectionShape instanceof Calibration)) return;

        let pixels = this.getDistance(this.selectionShape.points[0], this.selectionShape.points[1]);
        let mm = cm * 10;

        this.calibrationMultiplier = mm / (pixels * instance.pixelspacing);
        // setCookie("calibrationMultiplier", calibrationMultiplier); // TODO COOKIE

        this.setMeasure(MEASURE_NONE);
    }

    resetCalibration() {
        this.calibrationMultiplier = 1.0;
        // setCookie("calibrationMultiplier", calibrationMultiplier);

        this.setMeasure(MEASURE_NONE);
        app.viewer.drawImage(app.viewer.getViewport());
    }

}

class Double {
    static toString(nr, fractionDigits = 2) {
        let value = Number(nr);

        return value.toLocaleString(undefined, { maximumFractionDigits: fractionDigits, minimumFractionDigits: fractionDigits });
    }
}

class SelectionPoint {
    constructor(instance, x, y, marker = true) {
        this.instance = instance;
        this.x = x;
        this.y = y;
        this.marker = marker;
    }

    contains(x, y, touch) {
        let range = touch ? touchRange : mouseRange;
        range = range / this.instance.scale;
        return this.x - range <= x && this.x + range >= x && this.y - range <= y && this.y + range >= y;
    }
}

class TextPoint extends SelectionPoint {
    constructor(instance, x = 0, y = 0) {
        super(instance, x, y, false);
        this.text = "";
        this.moved = false;
        this.shape = null;
        this.fillStyle = "white";
    }

    setText(gg, text) {
        this.text = text;
    }

    contains(x, y) {
        let rect = this.getRect();

        return rect.x <= x && rect.x + rect.width >= x && rect.y <= y && rect.y + rect.height >= y;
    }

    getRect() {
        let x = this.x;
        let y = this.y;

        let extraWidth = (mouseRange / 2) / this.instance.scale;
        let extraHeight = mouseRange / this.instance.scale;

        let height = fontHeight / this.instance.scale;

        let canvas = document.createElement("canvas");
        let ff = canvas.getContext("2d");
        ff.font = height + "px Verdana";
        let width = ff.measureText(this.text).width;

        return { x: x, y: y - extraHeight, width: width + extraWidth, height: height + extraHeight };
    }

    init(shape, x, y) {
        if (!this.moved) {
            this.shape = shape;
            this.x = x;
            this.y = y;
        }
    }
}

class Shape {
    constructor(instance) {
        this.instance = instance;
        this.selected = true;
        this.deleted = false;
        this.points = [];
        this.rotation = instance.rotation;
        this.flipped = instance.flipped;
    }

    attemptSelection(x, y, touch) {
        let result;
        for (let point of this.points) {
            if (point != null && point.contains(x, y, touch)) {
                result = point;
            }
        }
        return result;
    }

    drawSelectionCircles(gg, exporting = false) {
        if (app.viewer.currentSubmenu === SUBMENU_MEASURES && !exporting) {
            if (this.selected) {
                for (let point of this.points) {
                    app.viewer.measurer.drawSelectionPoint(gg, point);
                }
            }
        }
    }

    movePoint(x, y, source, target) {
        let deltax = x - source.x;
        let deltay = y - source.y;

        target.x += deltax;
        target.y += deltay;
    }

    moveText(x, y, textpoint) {
        textpoint.x = x;
        textpoint.y = y;
        textpoint.moved = true;
    }

    drawMarkers(gg, cross = false) {
        for (let point of this.points) {
            if (point != null && point.marker) app.viewer.measurer.drawMarker(gg, point, cross);
        }
    }

    isComplete() { return true; }

    getMiddle(start, end, middle) {
        if (start != null && end != null) {
            let x = (start.x + end.x) / 2;
            let y = (start.y + end.y) / 2;
            if (middle == null) {
                return new SelectionPoint(this.instance, x, y, false);
            } else {
                middle.x = x;
                middle.y = y;
                return middle;
            }
        } else {
            return null;
        }
    }

    updateLabels() {

    }
}

class Line extends Shape {
    constructor(point) {
        super(point.instance);
        this.points.push(point); // [0] start
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] end

        this.points.push(null); // [2] middle

        this.points.push(new TextPoint(point.instance)); // [3] textpoint

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    calc() {
        const start = this.points[0];
        const end = this.points[1];
        const middle = this.points[2];

        this.points[2] = this.getMiddle(start, end, middle);
    }

    move(point, x, y) {
        const start = this.points[0];
        const end = this.points[1];
        const middle = this.points[2];
        const textpoint = this.points[3];

        if (point === start || point === end) {
            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === middle) {
            this.movePoint(x, y, middle, start);
            this.movePoint(x, y, middle, end);

            this.calc();
        } else if (point === textpoint) {
            this.moveText(x, y, textpoint);
        }
    }

    draw(gg) {
        const start = this.points[0];
        const end = this.points[1];
        let textpoint = this.points[3];

        this.drawSelectionCircles(gg);

        app.viewer.measurer.drawLine(gg, start, end);

        if ($("#radLinesNone:checked").val() === "on") {
            textpoint.moved = false;
            textpoint.setText(gg, "");
        } else {
            let distance = app.viewer.measurer.getDistance(start, end);

            textpoint.init(this, end.x + mouseRange / this.instance.scale, end.y);

            if ($("#radMetric:checked").val() === "on") textpoint.setText(gg, app.viewer.measurer.getMetricDistance(distance));
            else textpoint.setText(gg, app.viewer.measurer.getImperialDistance(distance));

            app.viewer.measurer.drawTextPoint(gg, textpoint);
        }

        this.drawMarkers(gg);
    }

    getSummary() {
        const start = this.points[0];
        const end = this.points[1];

        return "Line:" + start.x + "," + start.y + "-" + end.x + "," + end.y;
    }
}

class Circle extends Shape {
    constructor(point) {
        super(point.instance);

        this.points.push(point); // [0] edge
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] center
        this.points.push(null); // [2] middle
        this.points.push(new TextPoint(point.instance)); // [2] textpoint

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    calc() {
        const edge = this.points[0];
        const center = this.points[1];
        const middle = this.points[2];

        this.points[2] = this.getMiddle(edge, center, middle);
    }

    move(point, x, y) {
        const edge = this.points[0];
        const center = this.points[1];
        const middle = this.points[2];
        const textpoint = this.points[3];

        if (point === center) {
            // this.movePoint(x, y, center, edge);
            center.x = x;
            center.y = y;
            this.calc();
        } else if (point === edge) {
            edge.x = x;
            edge.y = y;
            this.calc();
        } else if (point === middle) {
            this.movePoint(x, y, middle, edge);
            this.movePoint(x, y, middle, center);

            this.calc();
        }
        else if (point === textpoint) {
            this.moveText(x, y, textpoint);
        }
    }

    draw(gg) {
        const edge = this.points[0];
        const center = this.points[1];
        const textpoint = this.points[3];

        this.drawSelectionCircles(gg);

        let radius = app.viewer.measurer.getDistance(center, edge);

        app.viewer.measurer.drawCircle(gg, center, radius);

        if ($("#radCirclesNone:checked").val() === "on") {
            textpoint.moved = false;
            textpoint.setText(gg, "");
        } else {
            textpoint.init(this, center.x + radius + mouseRange / this.instance.scale, center.y);

            let metric = $("#radMetric:checked").val() === "on";

            if ($("#radCirclesRadius:checked").val() === "on") {
                app.viewer.measurer.drawLine(gg, center, edge, true);
                let score = metric ? app.viewer.measurer.getMetricDistance(radius) : app.viewer.measurer.getImperialDistance(radius);
                textpoint.setText(gg, "r = " + score);
            } else if ($("#radCirclesDiameter:checked").val() === "on") {
                app.viewer.measurer.drawLine(gg, edge, { x: 2 * center.x - edge.x, y: 2 * center.y - edge.y }, true); // from edge to opposite edge (calculated via center)
                let score =  metric ? app.viewer.measurer.getMetricDistance(radius * 2) : app.viewer.measurer.getImperialDistance(radius * 2);
                textpoint.setText(gg, "D = " + score);
            } else if ($("#radCirclesCircumference:checked").val() === "on") {
                let score = metric ? app.viewer.measurer.getMetricDistance(radius * 2 * Math.PI) : app.viewer.measurer.getImperialDistance(radius * 2 * Math.PI);
                textpoint.setText(gg, "c = " + score);
            }  else if ($("#radCirclesSurface:checked").val() === "on") {
                let score = metric ? app.viewer.measurer.getMetricSurface(radius * radius * Math.PI) : app.viewer.measurer.getImperialSurface(radius * radius * Math.PI);
                textpoint.setText(gg, "S = " + score);
            }

            app.viewer.measurer.drawTextPoint(gg, textpoint);

        }

        this.drawMarkers(gg);
    }
}

class Dot extends Shape {
    constructor(point) {
        super(point.instance);
        this.points.push(point); // [0] dot
        this.points.push(new TextPoint(point.instance)); // [1] text
    }

    move(point, x, y) {
        const dot = this.points[0];
        const textpoint = this.points[1];

        if (point === dot) {
            dot.x = x;
            dot.y = y;
        } else if (point === textpoint) {
            this.moveText(x, y, textpoint);
        }
    }

    draw(gg ) {
        const dot = this.points[0];
        const textpoint = this.points[1];

        this.drawSelectionCircles(gg);

        if ($("#radDotsNone:checked").val() === "on") {
            textpoint.moved = false;
            textpoint.setText(gg, "");
        } else {
            textpoint.init(this, dot.x + mouseRange / this.instance.scale, dot.y);

            textpoint.setText(gg, "(" + Double.toString(dot.x, 0) + "," + Double.toString(dot.y, 0) + ")");
            app.viewer.measurer.drawTextPoint(gg, textpoint);
        }

        this.drawMarkers(gg, true);
    }
}

class Angle extends Shape {
    constructor(point) {
        super(point.instance);

        this.points.push(point); // [0] start
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] edge
        this.points.push(null); // [2] end

        this.points.push(null); // [3] middle1
        this.points.push(null); // [4] middle2
        // this.points.push(null); // [5] center

        this.points.push(new TextPoint(point.instance)); // [6] textpoint

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    next(point) {
        this.points[2] = point;
    }

    calc() {
        const start = this.points[0];
        const edge = this.points[1];
        const end = this.points[2];
        const middle1 = this.points[3];
        const middle2 = this.points[4];
        // const center = this.points[5];

        this.points[3] = this.getMiddle(start, edge, middle1);
        this.points[4] = this.getMiddle(end, edge, middle2);
        // this.points[5] = this.getMiddle(middle1, middle2, center);
    }

    move(point, x, y) {
        const start = this.points[0];
        const edge = this.points[1];
        const end = this.points[2];
        const middle1 = this.points[3];
        const middle2 = this.points[4];
        // const center = this.points[5];
        const textpoint = this.points[5];

        if (point === start || point === edge || point === end) {
            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === middle1) {
            this.movePoint(x, y, middle1, start);
            this.movePoint(x, y, middle1, edge);

            this.calc();
        } else if (point === middle2) {
            this.movePoint(x, y, middle2, end);
            this.movePoint(x, y, middle2, edge);

            this.calc();
        }
            // else if (point === center) {
            //     this.movePoint(x, y, center, start);
            //     this.movePoint(x, y, center, edge);
            //     this.movePoint(x, y, center, end);
            //
            //     this.calc();
        // }
        else if (point === textpoint) {
            this.moveText(x, y, textpoint);
        }
    }

    draw(gg) {
        const start = this.points[0];
        const edge = this.points[1];
        const end = this.points[2];
        const textpoint = this.points[5];

        this.drawSelectionCircles(gg);

        app.viewer.measurer.drawLine(gg, start, edge);

        if (end != null) {
            app.viewer.measurer.drawLine(gg, edge, end);

            if ($("#radAnglesNone:checked").val() === "on") {
                textpoint.moved = false;
                textpoint.setText(gg, "");
            } else {
                textpoint.init(this, edge.x + mouseRange / this.instance.scale, edge.y);

                if ($("#radInnerAngle:checked").val() === "on") {
                    let startAngle = app.viewer.measurer.getAngleXAxis(edge, start);
                    let endAngle = app.viewer.measurer.getAngleXAxis(edge, end);
                    let angle = (endAngle - startAngle + RAD360) % RAD360;

                    app.viewer.measurer.drawInnerAngleArc(gg, edge, startAngle, endAngle, angle > RAD180);

                    if (angle > RAD180) angle = RAD360 - angle;

                    textpoint.setText(gg, app.viewer.measurer.toDegrees(angle).toFixed(1) + charDegree);
                } else {
                    let startAngle = app.viewer.measurer.getAngleXAxis(edge, start);
                    let endAngle = app.viewer.measurer.getAngleXAxis(edge, end);
                    let angle = (endAngle - startAngle + RAD360) % RAD360;

                    app.viewer.measurer.drawOuterAngleArc(gg, edge, startAngle, endAngle, angle <= RAD180);

                    if (angle < RAD180) angle += RAD180;

                    textpoint.setText(gg, app.viewer.measurer.toDegrees(angle).toFixed(1) + charDegree);
                }

                app.viewer.measurer.drawTextPoint(gg, textpoint);
            }
        }

        this.drawMarkers(gg);
    }

    isComplete() {
        const end = this.points[2];
        return end != null;
    }

    updateLabels() {
        const edge = this.points[1];
        const end = this.points[2];

        $("#lblAngles1").css("color", this.deleted || (edge != null && end != null) ? "lightgray" : "gray");
        $("#lblAngles2").css("color", !this.deleted && edge != null && end == null ? "lightgray" : "gray");
    }

}

class Cobb extends Shape {
    constructor(point) {
        super(point.instance);

        this.points.push(point); // [0] start1
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] end1
        this.points.push(null); // [2] start2
        this.points.push(null); // [3] end2

        this.points.push(null); // [4] middle1
        this.points.push(null); // [5] middle2
        this.points.push(null); // [6] center

        this.points.push(new TextPoint(point.instance)); // [7] textpoint

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    next(point) {
        this.points[2] = point;
        this.points[3] = new SelectionPoint(point.instance, point.x, point.y);

        app.viewer.measurer.selectionPoint = this.points[3];
    }

    calc() {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const middle1 = this.points[4];
        const middle2 = this.points[5];
        const center = this.points[6];

        this.points[4] = this.getMiddle(start1, end1, middle1);
        this.points[5] = this.getMiddle(start2, end2, middle2);
        this.points[6] = this.getMiddle(middle1, middle2, center);
    }

    move(point, x, y) {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const middle1 = this.points[4];
        const middle2 = this.points[5];
        const center = this.points[6];
        const textpoint = this.points[7];

        if (point === start1 || point === end1 || point === start2 || point === end2) {
            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === middle1) {
            this.movePoint(x, y, middle1, start1);
            this.movePoint(x, y, middle1, end1);

            this.calc();
        } else if (point === middle2) {
            this.movePoint(x, y, middle2, start2);
            this.movePoint(x, y, middle2, end2);

            this.calc();
        } else if (point === center) {
            this.movePoint(x, y, center, start1);
            this.movePoint(x, y, center, end1);
            this.movePoint(x, y, center, start2);
            this.movePoint(x, y, center, end2);

            this.calc();
        } else if (point === textpoint) {
            this.moveText(x, y, textpoint);
        }
    }

    draw(gg) {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const middle1 = this.points[4];
        const middle2 = this.points[5];
        const center = this.points[6];
        const textpoint = this.points[7];

        this.drawSelectionCircles(gg);

        app.viewer.measurer.drawLine(gg, start1, end1);

        if (start2 != null && end2 != null) {
            app.viewer.measurer.drawLine(gg, start2, end2);
            app.viewer.measurer.drawLine(gg, middle1, middle2, true);

            if (center != null) {
                textpoint.init(this, center.x + mouseRange / this.instance.scale, center.y);
                let angle = app.viewer.measurer.getAcuteAngle(start1, end1, start2, end2);
                textpoint.setText(gg, "Cobb: " + app.viewer.measurer.toDegrees(angle).toFixed(1) + charDegree);

                app.viewer.measurer.drawTextPoint(gg, textpoint);
            }
        }

        this.drawMarkers(gg);
    }

    isComplete() {
        const start2 = this.points[2];
        return start2 != null;
    }

    updateLabels() {
        const end1 = this.points[1];
        const end2 = this.points[3];

        $("#lblCobb1").css("color", this.deleted || (end1 != null && end2 != null) ? "lightgray" : "gray");
        $("#lblCobb2").css("color", !this.deleted && end1 != null && end2 == null ? "lightgray" : "gray");
    }
}

class Parallel extends Shape {
    constructor(point) {
        super(point.instance);

        this.points.push(point); // [0] start1
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] end1
        this.points.push(null); // [2] start2
        this.points.push(null); // [3] end2

        this.points.push(null); // [4] middle1
        this.points.push(null); // [5] middle2
        this.points.push(null); // [6] center

        this.points.push(new TextPoint(point.instance)); // [7] textpoint

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    next(point) {
        this.points[2] = point;
        this.points[3] = new SelectionPoint(point.instance, point.x, point.y);

        app.viewer.measurer.selectionPoint = this.points[3];
    }

    calc() {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const middle1 = this.points[4];
        const middle2 = this.points[5];
        const center = this.points[6];

        this.points[4] = this.getMiddle(start1, end1, middle1);
        this.points[5] = this.getMiddle(start2, end2, middle2);

        if (end2 != null) {
            let intersection = app.viewer.measurer.getPointIntersection(start2, end2, middle1);
            this.points[6] = this.getMiddle(middle1, intersection, center);
        }
    }

    move(point, x, y) {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const middle1 = this.points[4];
        const middle2 = this.points[5];
        const center = this.points[6];
        const textpoint = this.points[7];

        if (point === start1) {
            if (start2 == null || end2 == null || end1 == null) {
                point.x = x;
                point.y = y;
                this.calc();
            } else {
                let slope = app.viewer.measurer.getSlope(start2, end2);
                if (slope < 1) {
                    let x2 = x;
                    let y2 = slope * x2 - slope * end1.x + end1.y;
                    point.x = x2;
                    point.y = y2;
                } else {
                    let y2 = y;
                    let x2 = (y2 - end1.y + slope * end1.x) / slope;
                    point.y = y2;
                    point.x = x2;
                }

                this.calc();
            }
        } else if (point === end1) {
            if (start2 == null || end2 == null || start1 == null) {
                point.x = x;
                point.y = y;
                this.calc();
            } else {
                let slope = app.viewer.measurer.getSlope(start2, end2);
                if (slope < 1) {
                    let x2 = x;
                    let y2 = slope * x2 - slope * start1.x + start1.y;
                    point.x = x2;
                    point.y = y2;
                } else {
                    let y2 = y;
                    let x2 = (y2 - start1.y + slope * start1.x) / slope;
                    point.y = y2;
                    point.x = x2;
                }
                this.calc();
            }
        } else if (point === start2) {
            if (start1 == null || end1 == null || end2 == null) {
                point.x = x;
                point.y = y;
                this.calc();
            } else {
                let slope = app.viewer.measurer.getSlope(start1, end1);
                if (slope < 1) {
                    let x2 = x;
                    let y2 = slope * x2 - slope * end2.x + end2.y;
                    point.x = x2;
                    point.y = y2;
                } else {
                    let y2 = y;
                    let x2 = (y2 - end2.y + slope * end2.x) / slope;
                    point.y = y2;
                    point.x = x2;
                }
                this.calc();
            }
        } else if (point === end2) {
            if (start1 == null || end1 == null || start2 == null) {
                point.x = x;
                point.y = y;
                this.calc();
            } else {
                let slope = app.viewer.measurer.getSlope(start1, end1);
                if (slope < 1) {
                    let x2 = x;
                    let y2 = slope * x2 - slope * start2.x + start2.y;
                    point.x = x2;
                    point.y = y2;
                } else {
                    let y2 = y;
                    let x2 = (y2 - start2.y + slope * start2.x) / slope;
                    point.y = y2;
                    point.x = x2;
                }
                this.calc();
            }
        } else if (point === middle1) {
            this.movePoint(x, y, middle1, start1);
            this.movePoint(x, y, middle1, end1);

            this.calc();
        } else if (point === middle2) {
            this.movePoint(x, y, middle2, start2);
            this.movePoint(x, y, middle2, end2);

            this.calc();
        } else if (point === center) {
            this.movePoint(x, y, center, start1);
            this.movePoint(x, y, center, end1);
            this.movePoint(x, y, center, start2);
            this.movePoint(x, y, center, end2);

            this.calc();
        } else if (point === textpoint) {
            this.moveText(x, y, textpoint);
        }
    }

    draw(gg) {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const middle1 = this.points[4];
        const center = this.points[6];
        const textpoint = this.points[7];

        this.drawSelectionCircles(gg);

        app.viewer.measurer.drawLine(gg, start1, end1);

        if (start2 != null && end2 != null) {
            app.viewer.measurer.drawLine(gg, start2, end2);

            if ($("#radParallelsNone:checked").val() === "on") {
                textpoint.moved = false;
                textpoint.setText(gg, "");
            } else {
                let intersection = app.viewer.measurer.getPointIntersection(start2, end2, middle1);
                app.viewer.measurer.drawLine(gg, middle1, intersection, true);

                if (center != null) {
                    textpoint.init(this, center.x + mouseRange / this.instance.scale, center.y);

                    let distance = (middle1 == null) ? 0 : app.viewer.measurer.getDistance(middle1, intersection);

                    let metric = $("#radMetric:checked").val() === "on";

                    textpoint.setText(gg, metric ? app.viewer.measurer.getMetricDistance(distance, 2) : app.viewer.measurer.getImperialDistance(distance, 2));

                    app.viewer.measurer.drawTextPoint(gg, textpoint);
                }
            }
        }

        this.drawMarkers(gg);
    }

    isComplete() {
        return this.points[2] != null;
    }

    updateLabels() {
        const end1 = this.points[1];
        const end2 = this.points[3];

        $("#lblParallels1").css("color", this.deleted || (end1 != null && end2 != null) ? "lightgray" : "gray");
        $("#lblParallels2").css("color", !this.deleted && end1 != null && end2 == null ? "lightgray" : "gray");
    }
}

class Perpendicular extends Shape {
    constructor(point) {
        super(point.instance);

        this.points.push(point); // [0] start1
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] end1
        this.points.push(null); // [2] start2
        this.points.push(null); // [3] end2

        this.points.push(null); // [4] middle1
        this.points.push(null); // [5] middle2
        this.points.push(null); // [6] intersection

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    next(point) {
        this.points[2] = point;
        this.points[3] = new SelectionPoint(point.instance, point.x, point.y);

        app.viewer.measurer.selectionPoint = this.points[3];
    }

    calc() {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const middle1 = this.points[4];
        const middle2 = this.points[5];

        this.points[4] = this.getMiddle(start1, end1, middle1);
        this.points[5] = this.getMiddle(start2, end2, middle2);

        let intersection = app.viewer.measurer.getLineIntersection(start1, end1, start2, end2, this.instance);

        if (middle1 != null && middle2 != null)
            this.points[6] = new SelectionPoint(this.instance, intersection.x, intersection.y);

    }

    move(point, x, y) {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const middle1 = this.points[4];
        const middle2 = this.points[5];
        const intersection = this.points[6];

        if (point === start1 || point === end1) {
            if (start2 != null && end2 != null) {
                let a0 = (end2.y - start2.y) / (end2.x - start2.x);
                let a1 = -1 / a0; // this ensures perpendicularity
                let b1 = start1.y - (a1 * start1.x);
                if (Math.abs(a0) < 1.0) x = (y - b1) / a1; // almost horizontal perpendicular, making it hard to choose via a point on the x-axis
                else y = a1 * x + b1;
            }
            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === start2 || point === end2) {
            let a0 = (end1.y - start1.y) / (end1.x - start1.x);
            let a1 = -1 / a0; // this ensures perpendicularity
            let b1 = start2.y - (a1 * start2.x);
            if (Math.abs(a0) < 1.0) x = (y - b1) / a1; // almost horizontal perpendicular, making it hard to choose via a point on the x-axis
            else y = a1 * x + b1;

            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === middle1) {
            this.movePoint(x, y, middle1, start1);
            this.movePoint(x, y, middle1, end1);

            this.calc();
        } else if (point === middle2) {
            this.movePoint(x, y, middle2, start2);
            this.movePoint(x, y, middle2, end2);

            this.calc();
        } else if (point === intersection) {
            this.movePoint(x, y, intersection, start1);
            this.movePoint(x, y, intersection, end1);
            this.movePoint(x, y, intersection, start2);
            this.movePoint(x, y, intersection, end2);
            this.movePoint(x, y, intersection, middle1);
            this.movePoint(x, y, intersection, middle2);
            this.movePoint(x, y, intersection, intersection);
        }
    }

    draw(gg) {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const intersection = this.points[6];

        this.drawSelectionCircles(gg);

        app.viewer.measurer.drawLine(gg, start1, intersection, true);
        app.viewer.measurer.drawLine(gg, start2, intersection, true);

        app.viewer.measurer.drawLine(gg, start1, end1);
        app.viewer.measurer.drawLine(gg, start2, end2);

        this.drawMarkers(gg);
    }

    isComplete() {
        return this.points[2] != null;
    }

    updateLabels() {
        const end1 = this.points[1];
        const end2 = this.points[3];

        $("#lblPerpendiculars1").css("color", this.deleted || (end1 != null && end2 != null) ? "lightgray" : "gray");
        $("#lblPerpendiculars2").css("color", !this.deleted && end1 != null && end2 == null ? "lightgray" : "gray");
    }

}

class VHS extends Shape {
    constructor(point) {
        super(point.instance);

        this.points.push(point); // [0] start1
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] end1
        this.points.push(null); // [2] start2
        this.points.push(null); // [3] end2
        this.points.push(null); // [4] start3
        this.points.push(null); // [5] end3

        this.points.push(null); // [6] middle1
        this.points.push(null); // [7] middle2
        this.points.push(null); // [8] middle3
        this.points.push(null); // [9] center

        this.points.push(new TextPoint(point.instance)); // [10] textpoint

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    next(point) {
        if (this.points[2] == null) {
            this.points[2] = point;
            this.points[3] = new SelectionPoint(point.instance, point.x, point.y);
            app.viewer.measurer.selectionPoint = this.points[3];
        } else {
            this.points[4] = point;
            this.points[5] = new SelectionPoint(point.instance, point.x, point.y);
            app.viewer.measurer.selectionPoint = this.points[5];
        }
    }

    calc() {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const start3 = this.points[4];
        const end3 = this.points[5];
        const middle1 = this.points[6];
        const middle2 = this.points[7];
        const middle3 = this.points[8];
        const center = this.points[9];

        this.points[6] = this.getMiddle(start1, end1, middle1);
        this.points[7] = this.getMiddle(start2, end2, middle2);
        this.points[8] = this.getMiddle(start3, end3, middle3);

        const intersection = app.viewer.measurer.getLineIntersection(start1, end1, start2, end2);

        this.points[9] = this.getMiddle(intersection, middle3, center);
    }

    move(point, x, y) {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const start3 = this.points[4];
        const end3 = this.points[5];
        const middle1 = this.points[6];
        const middle2 = this.points[7];
        const middle3 = this.points[8];
        const center = this.points[9];
        const textpoint = this.points[10];

        if (point === start1 || point === end1) {
            if (start2 != null && end2 != null) {
                let a0 = (end2.y - start2.y) / (end2.x - start2.x);
                let a1 = -1 / a0; // this ensures
                let b1 = start1.y - (a1 * start1.x);
                if (Math.abs(a0) < 1.0) x = (y - b1) / a1; // almost horizontal perpendicular, making it hard to choose via a point on the x-axis
                else y = a1 * x + b1;
            }
            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === start2 || point === end2) {
            let a0 = (end1.y - start1.y) / (end1.x - start1.x);
            let a1 = -1 / a0; // this ensures perpendicularity
            let b1 = start2.y - (a1 * start2.x);
            if (Math.abs(a0) < 1.0) x = (y - b1) / a1; // almost horizontal perpendicular, making it hard to choose via a point on the x-axis
            else y = a1 * x + b1;

            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === start3 || point === end3) {
            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === middle1) {
            this.movePoint(x, y, middle1, start1);
            this.movePoint(x, y, middle1, end1);

            this.calc();
        } else if (point === middle2) {
            this.movePoint(x, y, middle2, start2);
            this.movePoint(x, y, middle2, end2);

            this.calc();
        } else if (point === middle3) {
            this.movePoint(x, y, middle3, start3);
            this.movePoint(x, y, middle3, end3);

            this.calc();
        } else if (point === center) {
            this.movePoint(x, y, center, start1);
            this.movePoint(x, y, center, end1);
            this.movePoint(x, y, center, start2);
            this.movePoint(x, y, center, end2);
            this.movePoint(x, y, center, start3);
            this.movePoint(x, y, center, end3);

            this.calc();
        } else if (point === textpoint) {
            this.moveText(x, y, textpoint);
        }
    }

    draw(gg) {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const start3 = this.points[4];
        const end3 = this.points[5];
        const middle3 = this.points[8];
        const center = this.points[9];
        const textpoint = this.points[10];

        this.drawSelectionCircles(gg);

        app.viewer.measurer.drawLine(gg, start1, end1);
        app.viewer.measurer.drawLine(gg, start2, end2);

        if (end3 != null) {
            app.viewer.measurer.drawLine(gg, start3, end3);

            const intersection = app.viewer.measurer.getLineIntersection(start1, end1, start2, end2);

            app.viewer.measurer.drawLine(gg, intersection, middle3, true);

            if (center != null) {
                textpoint.init(this, center.x + mouseRange / this.instance.scale, center.y);
                const score = (5 * (app.viewer.measurer.getDistance(start1, end1) + app.viewer.measurer.getDistance(start2, end2))) / app.viewer.measurer.getDistance(start3, end3);
                textpoint.setText(gg, "VHS: " + score.toFixed(1));

                app.viewer.measurer.drawTextPoint(gg, textpoint);
            }


        }

        this.drawMarkers(gg);
    }

    isComplete() {
        const end3 = this.points[5];
        return end3 != null;
    }

    updateLabels() {
        const end1 = this.points[1];
        const end2 = this.points[3];
        const end3 = this.points[5];

        $("#lblVHS1").css("color", this.deleted || (end1 != null && end2 != null && end3 != null) ? "lightgray" : "gray");
        $("#lblVHS2").css("color", !this.deleted && end1 != null && end2 == null && end3 == null ? "lightgray" : "gray");
        $("#lblVHS3").css("color", !this.deleted && end1 != null && end2 != null && end3 == null ? "lightgray" : "gray");
    }

}

class CTR extends Shape {
    constructor(point) {
        super(point.instance);

        this.points.push(point); // [0] start1
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] end1
        this.points.push(null); // [2] end2
        this.points.push(null); // [3] end3
        this.points.push(null); // [4] end4

        this.points.push(null); // [5] middle1
        this.points.push(null); // [6] center

        this.points.push(new TextPoint(point.instance)); // [7] textpoint

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    next(point) {
        if (this.points[2] == null) {
            this.points[2] = point;
        } else if (this.points[3] == null) {
            this.points[3] = point;
        } else {
            this.points[4] = point;
        }
    }

    calc() {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const end2 = this.points[2];
        const middle1 = this.points[5];
        const center = this.points[6];

        this.points[5] = this.getMiddle(start1, end1, middle1);

        if (end2 != null) {
            let intersection = app.viewer.measurer.getPointIntersection(start1, end1, end2);
            this.points[6] = this.getMiddle(intersection, end2, center);
        }
    }

    move(point, x, y) {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const end2 = this.points[2];
        const end3 = this.points[3];
        const end4 = this.points[4];
        const middle1 = this.points[5];
        const center = this.points[6];
        const textpoint = this.points[7];

        if (point === start1 || point === end1 || point === end2 || point === end3 || point === end4) {
            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === middle1) {
            this.movePoint(x, y, middle1, start1);
            this.movePoint(x, y, middle1, end1);

            this.calc();
        } else if (point === center) {
            this.movePoint(x, y, center, start1);
            this.movePoint(x, y, center, end1);
            this.movePoint(x, y, center, end2);
            this.movePoint(x, y, center, end3);
            this.movePoint(x, y, center, end4);

            this.calc();
        } else if (point === textpoint) {
            this.moveText(x, y, textpoint);
        }
    }

    draw(gg) {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const end2 = this.points[2];
        const end3 = this.points[3];
        const end4 = this.points[4];
        // const middle1 = this.points[5];
        const center = this.points[6];
        const textpoint = this.points[7];

        this.drawSelectionCircles(gg);

        app.viewer.measurer.drawLine(gg, start1, end1);

        if (end2 != null) {
            let intersection1 = app.viewer.measurer.getPointIntersection(start1, end1, end2);
            app.viewer.measurer.drawLine(gg, intersection1, end2, true);

            if (end3 != null) {
                let intersection2 = app.viewer.measurer.getPointIntersection(intersection1, end2, end3);
                app.viewer.measurer.drawLine(gg, intersection2, end3);

                if (end4 != null) {
                    let intersection3 = app.viewer.measurer.getPointIntersection(intersection1, end2, end4);
                    app.viewer.measurer.drawLine(gg, intersection3, end4);

                    textpoint.init(this, center.x + mouseRange / this.instance.scale, center.y);

                    const mtd = app.viewer.measurer.getDistance(start1, end1);
                    const mL = app.viewer.measurer.getDistance(intersection2, end3);
                    const mR = app.viewer.measurer.getDistance(intersection3, end4);

                    const score = (mL + mR) / mtd;
                    textpoint.setText(gg, "CTR: " + score.toFixed(2));

                    app.viewer.measurer.drawTextPoint(gg, textpoint);
                }
            }
        }

        this.drawMarkers(gg);
    }

    isComplete() {
        const end4 = this.points[4];
        return end4 != null;
    }

    updateLabels() {
        const end1 = this.points[1];
        const end2 = this.points[2];
        const end3 = this.points[3];
        const end4 = this.points[4];

        $("#lblCTR1").css("color", this.deleted || (end1 != null && end2 != null && end3 != null && end4 != null) ? "lightgray" : "gray");
        $("#lblCTR2").css("color", !this.deleted && end1 != null && end2 == null && end3 == null && end4 == null ? "lightgray" : "gray");
        $("#lblCTR3").css("color", !this.deleted && end1 != null && end2 != null && end3 == null && end4 == null ? "lightgray" : "gray");
        $("#lblCTR4").css("color", !this.deleted && end1 != null && end2 != null && end3 != null && end4 == null ? "lightgray" : "gray");
    }
}

class NOA extends Shape {
    constructor(point) {
        super(point.instance);

        this.points.push(point); // [0] edge1
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] center1
        this.points.push(null); // [2] edge2
        this.points.push(null); // [3] center2
        this.points.push(null); // [4] end1
        this.points.push(null); // [5] end2

        this.points.push(null); // [6] middle1
        this.points.push(null); // [7] middle2
        this.points.push(null); // [8] center

        this.points.push(new TextPoint(point.instance)); // [9] textpoint1
        this.points.push(new TextPoint(point.instance)); // [10] textpoint2

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    next(point) {
        if (this.points[2] == null) {
            this.points[2] = point;
            this.points[3] = new SelectionPoint(point.instance, point.x, point.y);
            app.viewer.measurer.selectionPoint = this.points[3];
        } else if (this.points[4] == null) {
            this.points[4] = point;
        } else if (this.points[5] == null) {
            this.points[5] = point;
        }
    }

    calc() {
        const center1 = this.points[1];
        const center2 = this.points[3];
        const end1 = this.points[4];
        const end2 = this.points[5];
        const middle1 = this.points[6];
        const middle2 = this.points[7];
        const center = this.points[8];

        if (end1 != null) this.points[6] = this.getMiddle(center1, end1, middle1);
        if (end2 != null) this.points[7] = this.getMiddle(center2, end2, middle2);

        if (center2 != null) this.points[8] = this.getMiddle(center1, center2, center);
    }

    move(point, x, y) {
        const edge1 = this.points[0];
        const center1 = this.points[1];
        const edge2 = this.points[2];
        const center2 = this.points[3];
        const end1 = this.points[4];
        const end2 = this.points[5];
        const middle1 = this.points[6];
        const middle2 = this.points[7];
        const center = this.points[8];
        const textpoint1 = this.points[9];
        const textpoint2 = this.points[10];

        if (point === edge1 || point === edge2 || point === end1 || point === end2 || point === center1 || point === center2) {
            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === middle1) {
            this.movePoint(x, y, middle1, center1);
            this.movePoint(x, y, middle1, edge1);
            this.movePoint(x, y, middle1, end1);

            this.calc();
        } else if (point === middle2) {
            this.movePoint(x, y, middle2, center2);
            this.movePoint(x, y, middle2, edge2);
            this.movePoint(x, y, middle2, end2);

            this.calc();
        } else if (point === center) {
            this.movePoint(x, y, center, center1);
            this.movePoint(x, y, center, edge1);
            this.movePoint(x, y, center, center2);
            this.movePoint(x, y, center, edge2);
            if (end1 != null) this.movePoint(x, y, center, end1);
            if (end2 != null) this.movePoint(x, y, center, end2);

            this.calc();
        } else if (point === textpoint1 || point === textpoint2) {
            this.moveText(x, y, point);
        }
    }

    draw(gg) {
        const edge1 = this.points[0];
        const center1 = this.points[1];
        const edge2 = this.points[2];
        const center2 = this.points[3];
        const end1 = this.points[4];
        const end2 = this.points[5];
        const textpoint1 = this.points[9];
        const textpoint2 = this.points[10];

        this.drawSelectionCircles(gg);

        let radius1 = app.viewer.measurer.getDistance(center1, edge1);
        app.viewer.measurer.drawCircle(gg, center1, radius1, true);

        if (center2 != null && edge2 != null) {
            let radius2 = app.viewer.measurer.getDistance(center2, edge2);
            app.viewer.measurer.drawCircle(gg, center2, radius2, true);
            app.viewer.measurer.drawLine(gg, center1, center2);
        }

        if (end1 != null) {
            app.viewer.measurer.drawLine(gg, center1, end1);

            textpoint1.init(this, center1.x + mouseRange / this.instance.scale, center1.y);

            let startAngle = app.viewer.measurer.getAngleXAxis(center1, end1);
            let endAngle = app.viewer.measurer.getAngleXAxis(center1, center2);
            let angle = (endAngle - startAngle + RAD360) % RAD360;

            app.viewer.measurer.drawInnerAngleArc(gg, center1, startAngle, endAngle, angle > RAD180);

            if (angle > RAD180) angle = RAD360 - angle;

            textpoint1.setText(gg, app.viewer.measurer.toDegrees(angle).toFixed(1) + charDegree);

            app.viewer.measurer.drawTextPoint(gg, textpoint1);
        }

        if (end2 != null) {
            app.viewer.measurer.drawLine(gg, center2, end2);

            textpoint2.init(this, center2.x + mouseRange / this.instance.scale, center2.y);

            let startAngle = app.viewer.measurer.getAngleXAxis(center2, end2);
            let endAngle = app.viewer.measurer.getAngleXAxis(center2, center1);
            let angle = (endAngle - startAngle + RAD360) % RAD360;

            app.viewer.measurer.drawInnerAngleArc(gg, center2, startAngle, endAngle, angle > RAD180);

            if (angle > RAD180) angle = RAD360 - angle;

            textpoint2.setText(gg, app.viewer.measurer.toDegrees(angle).toFixed(1) + charDegree);

            app.viewer.measurer.drawTextPoint(gg, textpoint2);
        }

        this.drawMarkers(gg);
    }

    isComplete() {
        return this.points[5] != null;
    }

    updateLabels() {
        const center1 = this.points[1];
        const center2 = this.points[3];
        const end1 = this.points[4];
        const end2 = this.points[5];

        $("#lblNOA1").css("color", this.deleted || (center1 != null && center2 != null && end1 != null && end2 != null) ? "lightgray" : "gray");
        $("#lblNOA2").css("color", !this.deleted && center1 != null && center2 == null && end1 == null && end2 == null ? "lightgray" : "gray");
        $("#lblNOA3").css("color", !this.deleted && center1 != null && center2 != null && end1 == null && end2 == null ? "lightgray" : "gray");
        $("#lblNOA4").css("color", !this.deleted && center1 != null && center2 != null && end1 != null && end2 == null ? "lightgray" : "gray");
    }

}

class TPA extends Shape {
    constructor(point) {
        super(point.instance);

        this.points.push(point); // [0] edge1
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] center1
        this.points.push(null); // [2] end1
        this.points.push(null); // [3] start2
        this.points.push(null); // [4] end2

        this.points.push(null); // [5] middle1
        this.points.push(null); // [6] middle2

        this.points.push(new TextPoint(point.instance)); // [7] textpoint

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    next(point) {
        if (this.points[2] == null) {
            this.points[2] = point;
        } else {
            this.points[3] = point;
            this.points[4] = new SelectionPoint(point.instance, point.x, point.y);
            app.viewer.measurer.selectionPoint = this.points[4];
        }
    }

    calc() {
        const center1 = this.points[1];
        const end1 = this.points[2];
        const start2 = this.points[3];
        const end2 = this.points[4];
        const middle1 = this.points[5];
        const middle2 = this.points[6];

        if (end1 != null) this.points[5] = this.getMiddle(center1, end1, middle1);
        if (end2 != null) this.points[6] = this.getMiddle(start2, end2, middle2);
    }

    move(point, x, y) {
        const edge1 = this.points[0];
        const center1 = this.points[1];
        const end1 = this.points[2];
        const start2 = this.points[3];
        const end2 = this.points[4];
        const middle1 = this.points[5];
        const middle2 = this.points[6];
        const textpoint = this.points[7];

        if (point === edge1 || point === end1 || point === start2 || point === end2 || point === center1) {
            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === middle1) {
            this.movePoint(x, y, middle1, center1);
            this.movePoint(x, y, middle1, edge1);
            this.movePoint(x, y, middle1, end1);
            this.calc();
        } else if (point === middle2) {
            // this.movePoint(x, y, middle2, end1);
            this.movePoint(x, y, middle2, start2);
            this.movePoint(x, y, middle2, end2);

            this.calc();
        } else if (point === textpoint) {
            this.moveText(x, y, textpoint);
        }
    }

    draw(gg) {
        const edge1 = this.points[0];
        const center1 = this.points[1];
        const end1 = this.points[2];
        const start2 = this.points[3];
        const end2 = this.points[4];
        const textpoint = this.points[7];

        this.drawSelectionCircles(gg);

        let radius = app.viewer.measurer.getDistance(center1, edge1);
        app.viewer.measurer.drawCircle(gg, center1, radius, true);

        if (end1 != null) {
            app.viewer.measurer.drawLine(gg, center1, end1);
        }

        if (end2 != null) {
            app.viewer.measurer.drawLine(gg, start2, end2);

            let intersection = app.viewer.measurer.getLineIntersection(center1, end1, start2, end2, this.instance);

            let a0 = (end1.y - center1.y) / (end1.x - center1.x);
            let a1 = -1 / a0; // this ensures perpendicularity
            let b1 = intersection.y - a1 * intersection.x;
            let length = app.viewer.measurer.getDistance(start2, end2) / 4;

            let x1 = intersection.x + length, y1 = intersection.y + length;
            if (Math.abs(a0) < 1.0) x1 = (y1 - b1) / a1;
            else y1 = a1 * x1 + b1;

            let x2 = intersection.x - length, y2 = intersection.y - length;
            if (Math.abs(a0) < 1.0) x2 = (y2 - b1) / a1;
            else y2 = a1 * x2 + b1;

            let start3 = { x: x1, y: y1, instance: this.instance };
            let end3 = { x: x2, y: y2, instance: this.instance };
            app.viewer.measurer.drawLine(gg, start3, end3, true);

            textpoint.init(this, end1.x + mouseRange / this.instance.scale, end1.y);

            let startAngle = app.viewer.measurer.getAngleXAxis(intersection, end3);
            let angle = app.viewer.measurer.getAcuteAngle(start2, end2, start3, end3);
            let endAngle = startAngle + angle;

            app.viewer.measurer.drawInnerAngleArc(gg, intersection, startAngle, endAngle, false);
            app.viewer.measurer.drawInnerAngleArc(gg, intersection, startAngle + RAD180, endAngle + RAD180, false);

            if (angle > RAD180) angle = RAD360 - angle;

            textpoint.setText(gg, "TPA: " + app.viewer.measurer.toDegrees(angle).toFixed(1) + charDegree);

            app.viewer.measurer.drawTextPoint(gg, textpoint);
        }

        this.drawMarkers(gg);
    }

    isComplete() {
        return this.points[4] != null;
    }

    updateLabels() {
        const center1 = this.points[1];
        const end1 = this.points[2];
        const end2 = this.points[4];

        $("#lblTPA1").css("color", this.deleted || (center1 != null && end1 != null && end2 != null) ? "lightgray" : "gray");
        $("#lblTPA2").css("color", !this.deleted && center1 != null && end1 == null && end2 == null ? "lightgray" : "gray");
        $("#lblTPA3").css("color", !this.deleted && center1 != null && end1 != null && end2 == null ? "lightgray" : "gray");
    }

}

class DI extends Shape {
    constructor(point) {
        super(point.instance);

        this.points.push(point); // [0] edge1
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] center1
        this.points.push(null); // [2] edge2
        this.points.push(null); // [3] center2

        this.points.push(new TextPoint(point.instance)); // [4] textpoint

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    next(point) {
        this.points[2] = point;
        this.points[3] = new SelectionPoint(point.instance, point.x, point.y);
        app.viewer.measurer.selectionPoint = this.points[3];
    }

    calc() {

    }

    move(point, x, y) {
        const edge1 = this.points[0];
        const center1 = this.points[1];
        const edge2 = this.points[2];
        const center2 = this.points[3];
        const textpoint = this.points[4];

        if (point === edge1 || point === edge2 || point === center1 || point === center2) {
            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === textpoint) {
            this.moveText(x, y, textpoint);
        }
    }

    draw(gg) {
        const edge1 = this.points[0];
        const center1 = this.points[1];
        const edge2 = this.points[2];
        const center2 = this.points[3];
        const textpoint = this.points[4];

        this.drawSelectionCircles(gg);

        let radius1 = app.viewer.measurer.getDistance(center1, edge1);
        app.viewer.measurer.drawCircle(gg, center1, radius1, true);

        if (center2 != null && edge2 != null) {
            let radius2 = app.viewer.measurer.getDistance(center2, edge2);
            app.viewer.measurer.drawCircle(gg, center2, radius2, true);
            app.viewer.measurer.drawLine(gg, center2, edge2);
            app.viewer.measurer.drawLine(gg, center1, center2);

            textpoint.init(this, center1.x + mouseRange / this.instance.scale, center1.y);

            let score = app.viewer.measurer.getDistance(center1, center2) / app.viewer.measurer.getDistance(center2, edge2);

            if (isFinite(score)) textpoint.setText(gg, "DI: " + score.toFixed(2));

            app.viewer.measurer.drawTextPoint(gg, textpoint);
        }

        this.drawMarkers(gg);
    }

    isComplete() {
        return this.points[2] != null;
    }

    updateLabels() {
        const center1 = this.points[1];
        const center2 = this.points[3];

        $("#lblDI1").css("color", this.deleted || (center1 != null && center2 != null) ? "lightgray" : "gray");
        $("#lblDI2").css("color", !this.deleted && center1 != null && center2 == null ? "lightgray" : "gray");
    }

}

class Vezzoni extends Shape {
    constructor(point) {
        super(point.instance);

        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [0] a1
        this.points.push(null); // [1] a2
        this.points.push(null); // [2] a3
        this.points.push(null); // [3] a4
        this.points.push(null); // [4] a5

        this.points.push(null); // [5] b1
        this.points.push(null); // [6] b2
        this.points.push(null); // [7] b3
        this.points.push(null); // [8] b4

        this.points.push(new TextPoint(point.instance)); // [9] textpoint

        app.viewer.measurer.selectionPoint = this.points[0];
    }

    next(point) {
        if (this.points[1] == null) this.points[1] = new SelectionPoint(point.instance, point.x, point.y);
        else if (this.points[2] == null) this.points[2] = new SelectionPoint(point.instance, point.x, point.y);
        else if (this.points[3] == null) this.points[3] = new SelectionPoint(point.instance, point.x, point.y);
        else if (this.points[4] == null) this.points[4] = new SelectionPoint(point.instance, point.x, point.y);
        else if (this.points[5] == null) this.points[5] = new SelectionPoint(point.instance, point.x, point.y);
        else if (this.points[6] == null) this.points[6] = new SelectionPoint(point.instance, point.x, point.y);
        else if (this.points[7] == null) this.points[7] = new SelectionPoint(point.instance, point.x, point.y);
        else if (this.points[8] == null) this.points[8] = new SelectionPoint(point.instance, point.x, point.y);

        app.viewer.measurer.selectionPoint = point;
    }

    calc() {

    }

    move(point, x, y) {
        const a1 = this.points[0];
        const a2 = this.points[1];
        const a3 = this.points[2];
        const a4 = this.points[3];
        const a5 = this.points[4];

        const b1 = this.points[5];
        const b2 = this.points[6];
        const b3 = this.points[7];
        const b4 = this.points[8];

        const textpoint = this.points[9];

        if (point === a1 || point === a2 || point === a3 || point === a4 || point === a5 || point === b1 || point === b2 || point === b3 || point === b4) {
            point.x = x;
            point.y = y;
        } else if (point === textpoint) {
            this.moveText(x, y, textpoint);
        }

    }

    draw(gg) {
        const textpoint = this.points[9];

        this.drawSelectionCircles(gg);

        let centerA, centerB;

        if (this.points[4] != null) {
            const a = this.points[0];
            const b = this.points[1];
            const c = this.points[2];
            const d = this.points[3];
            const e = this.points[4];

            let abc = app.viewer.measurer.get5PointCircleScore(a, b, c, d, e);
            let abd = app.viewer.measurer.get5PointCircleScore(a, b, d, c, e);
            let abe = app.viewer.measurer.get5PointCircleScore(a, b, e, c, d);
            let acd = app.viewer.measurer.get5PointCircleScore(a, c, d, b, e);
            let ace = app.viewer.measurer.get5PointCircleScore(a, c, e, b, d);
            let ade = app.viewer.measurer.get5PointCircleScore(a, d, e, b, c);

            let min = Math.min(abc, abd, abe, acd, ace, ade);

            if (min === abc) {
                centerA = app.viewer.measurer.drawCircleFromPoints(gg, a, b, c, this.instance);
            } else if (min === abd) {
                centerA = app.viewer.measurer.drawCircleFromPoints(gg, a, b, d, this.instance);
            } else if (min === abe) {
                centerA = app.viewer.measurer.drawCircleFromPoints(gg, a, b, e, this.instance);
            } else if (min === acd) {
                centerA = app.viewer.measurer.drawCircleFromPoints(gg, a, c, d, this.instance);
            } else if (min === ace) {
                centerA = app.viewer.measurer.drawCircleFromPoints(gg, a, c, e, this.instance);
            } else if (min === ade) {
                centerA = app.viewer.measurer.drawCircleFromPoints(gg, a, d, e, this.instance);
            }
        }

        if (this.points[8] != null) {
            const a = this.points[5];
            const b = this.points[6];
            const c = this.points[7];
            const d = this.points[8];

            let abc = app.viewer.measurer.get4PointCircleScore(a, b, c, d);
            let abd = app.viewer.measurer.get4PointCircleScore(a, b, d, c);
            let acd = app.viewer.measurer.get4PointCircleScore(a, c, d, b);

            let min = Math.min(abc, abd, acd);

            if (min === abc) {
                centerB = app.viewer.measurer.drawCircleFromPoints(gg, a, b, c, this.instance);
            } else if (min === abd) {
                centerB = app.viewer.measurer.drawCircleFromPoints(gg, a, b, d, this.instance);
            } else if (min === acd) {
                centerB = app.viewer.measurer.drawCircleFromPoints(gg, a, c, d, this.instance);
            }
        }

        if (centerA != null && centerB != null) {
            app.viewer.measurer.drawLine(gg, centerA, centerB);
            app.viewer.measurer.drawMarker(gg, centerA);
            app.viewer.measurer.drawMarker(gg, centerB);

            textpoint.init(this, centerA.x + mouseRange / this.instance.scale, centerA.y);

            let score = app.viewer.measurer.getDistance(centerA, centerB) / app.viewer.measurer.getDistance(centerB, this.points[5]);

            if (isFinite(score)) textpoint.setText(gg, "VEZ: " + score.toFixed(2));

            app.viewer.measurer.drawTextPoint(gg, textpoint);
        }

        this.drawMarkers(gg, true);
    }

    isComplete() {
        return this.points[8] != null;
    }

    updateLabels() {
        // const a1 = this.points[0];
        const e1 = this.points[4];
        const e2 = this.points[8];

        $("#lblVEZ1").css("color", this.deleted || e1 == null || (e2 != null) ? "lightgray" : "gray");
        $("#lblVEZ2").css("color", !this.deleted && e1 != null && e2 == null ? "lightgray" : "gray");
    }

}

class TTA extends Shape {
    constructor(point) {
        super(point.instance);

        this.points.push(point); // [0] start1
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] end1
        this.points.push(null); // [2] start2
        this.points.push(null); // [3] end2
        this.points.push(null); // [4] point3

        this.points.push(null); // [5] middle1
        this.points.push(null); // [6] middle2

        this.points.push(new TextPoint(point.instance)); // [7] textpoint

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    next(point) {
        if (this.points[2] == null) {
            this.points[2] = point;
            this.points[3] = new SelectionPoint(point.instance, point.x, point.y);
            app.viewer.measurer.selectionPoint = this.points[3];
        } else {
            this.points[4] = point;
            app.viewer.measurer.selectionPoint = this.points[4];
        }
    }

    calc() {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const middle1 = this.points[5];
        const middle2 = this.points[6];

        if (end1 != null) this.points[5] = this.getMiddle(start1, end1, middle1);
        if (end2 != null) this.points[6] = this.getMiddle(start2, end2, middle2);
    }

    move(point, x, y) {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const point3 = this.points[4];
        const middle1 = this.points[5];
        const middle2 = this.points[6];
        const textpoint = this.points[7];

        if (point === start1 || point === end1 || point === start2 || point === end2 || point === point3) {
            point.x = x;
            point.y = y;
            this.calc();
        } else if (point === middle1) {
            this.movePoint(x, y, middle1, start1);
            this.movePoint(x, y, middle1, end1);
            this.calc();
        } else if (point === middle2) {
            this.movePoint(x, y, middle2, start2);
            this.movePoint(x, y, middle2, end2);

            this.calc();
        } else if (point === textpoint) {
            this.moveText(x, y, textpoint);
        }
    }

    draw(gg) {
        const start1 = this.points[0];
        const end1 = this.points[1];
        const start2 = this.points[2];
        const end2 = this.points[3];
        const point3 = this.points[4];
        const textpoint = this.points[7];

        this.drawSelectionCircles(gg);

        app.viewer.measurer.drawLine(gg, start1, end1);
        app.viewer.measurer.drawLine(gg, start2, end2);

        if (point3 != null) {
            let yAngle = app.viewer.measurer.getAngleYAxis(start1, end1);
            let slope = yAngle > 0 ? Math.tan(app.viewer.measurer.toRadians(45 + app.viewer.measurer.toDegrees(app.viewer.measurer.getAngleYAxis(start1, end1)))) : Math.tan(app.viewer.measurer.toRadians(135 + app.viewer.measurer.toDegrees(app.viewer.measurer.getAngleYAxis(start1, end1))));

            let x2 = point3.x + 100;
            let y2 = slope * x2 - slope * point3.x + point3.y;
            let tempPoint = {x: x2, y: y2}; // arbitrary point, to make a line, using the calculated slope

            let point4 = app.viewer.measurer.getLineIntersection(point3, tempPoint, start2, end2, this.instance);
            app.viewer.measurer.drawLine(gg, point3, point4, true);

            let point5 = app.viewer.measurer.getPointIntersection(start1, end1, point3, this.instance);
            // drawLine(gg, point3, point5, true);

            let point6 = app.viewer.measurer.getPointIntersection(start2, end2, point3, this.instance);
            app.viewer.measurer.drawLine(gg, point3, point6, true);

            slope = app.viewer.measurer.getSlope(point3, point6);
            x2 = point4.x + 100;
            y2 = slope * x2 - slope * point4.x + point4.y;
            tempPoint = {x: x2, y: y2}; // arbitrary point, to make a line, using the calculated slope

            let point7 = app.viewer.measurer.getLineIntersection(point3, point5, tempPoint, point4, this.instance);
            app.viewer.measurer.drawLine(gg, point3, point7, false);

            app.viewer.measurer.drawLine(gg, point7, point5, true);
            app.viewer.measurer.drawMarker(gg, point7);

            textpoint.init(this, point7.x + mouseRange / this.instance.scale, point7.y);

            const score = app.viewer.measurer.getDistance(point3, point7);
            const metric = $("#radMetric:checked").val() === "on";
            const tta = metric ? app.viewer.measurer.getMetricDistance(score, 2) : app.viewer.measurer.getImperialDistance(score, 2);
            textpoint.setText(gg, "TTA: " + tta);

            app.viewer.measurer.drawTextPoint(gg, textpoint);
        }

        this.drawMarkers(gg);
    }

    isComplete() {
        return this.points[4] != null;
    }

    updateLabels() {
        const end1 = this.points[1];
        const end2 = this.points[3];
        const point3 = this.points[4];

        $("#lblTTA1").css("color", this.deleted || (end1 != null && end2 != null && point3 != null) ? "lightgray" : "gray");
        $("#lblTTA2").css("color", !this.deleted && end1 != null && end2 == null && point3 == null ? "lightgray" : "gray");
        $("#lblTTA3").css("color", !this.deleted && end1 != null && end2 != null && point3 == null ? "lightgray" : "gray");
    }

}

class Calibration extends Shape {
    constructor(point) {
        super(point.instance);
        this.points.push(point); // [0] start
        this.points.push(new SelectionPoint(point.instance, point.x, point.y)); // [1] end

        app.viewer.measurer.selectionPoint = this.points[1];
    }

    calc() {}

    next() {}

    move(point, x, y) {
        point.x = x;
        point.y = y;
        this.calc();
    }

    draw(gg) {
        const start = this.points[0];
        const end = this.points[1];

        this.drawSelectionCircles(gg);

        app.viewer.measurer.drawLine(gg, start, end);

        this.drawMarkers(gg);
    }

    isComplete() {
        return false;
    }

    updateLabels() {
        const end1 = this.points[1];

        $("#lblCalibration1").css("color", this.deleted ? "lightgray" : "gray");
        $("#lblCalibration2").css("color", !this.deleted && end1 != null ? "lightgray" : "gray");
    }
}
