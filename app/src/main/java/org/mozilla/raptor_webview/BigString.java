package org.mozilla.raptor_webview;

public class BigString {
    public static String MEASURE_JS = "/* This Source Code Form is subject to the terms of the Mozilla Public\n" +
            " * License, v. 2.0. If a copy of the MPL was not distributed with this\n" +
            " * file, You can obtain one at http://mozilla.org/MPL/2.0/. */\n" +
            "console.log(\"XXX XXX\");\n" +
            "\n" +
            // "setInterval(function () { console.log('interval'); }, 10);\n" +
            "\n" +
            "\n" +
            "// content script for use with pageload tests\n" +
            "var perfData = window.performance;\n" +
            "var gRetryCounter = 0;\n" +
            "\n" +
            "// measure hero element; must exist inside test page;\n" +
            "// supported on: Firefox, Chromium, Geckoview\n" +
            "// default only; this is set via control server settings json\n" +
            "var getHero = false;\n" +
            "var heroesToCapture = [];\n" +
            "\n" +
            "// measure time-to-first-non-blank-paint\n" +
            "// supported on: Firefox, Geckoview\n" +
            "// note: this browser pref must be enabled:\n" +
            "// dom.performance.time_to_non_blank_paint.enabled = True\n" +
            "// default only; this is set via control server settings json\n" +
            "var getFNBPaint = false;\n" +
            "\n" +
            "// measure time-to-first-contentful-paint\n" +
            "// supported on: Firefox, Chromium, Geckoview\n" +
            "// note: this browser pref must be enabled:\n" +
            "// dom.performance.time_to_contentful_paint.enabled = True\n" +
            "// default only; this is set via control server settings json\n" +
            "var getFCP = false;\n" +
            "\n" +
            "// measure domContentFlushed\n" +
            "// supported on: Firefox, Geckoview\n" +
            "// note: this browser pref must be enabled:\n" +
            "// dom.performance.time_to_dom_content_flushed.enabled = True\n" +
            "// default only; this is set via control server settings json\n" +
            "var getDCF = false;\n" +
            "\n" +
            "// measure TTFI\n" +
            "// supported on: Firefox, Geckoview\n" +
            "// note: this browser pref must be enabled:\n" +
            "// dom.performance.time_to_first_interactive.enabled = True\n" +
            "// default only; this is set via control server settings json\n" +
            "var getTTFI = false;\n" +
            "\n" +
            "// supported on: Firefox, Chromium, Geckoview\n" +
            "// default only; this is set via control server settings json\n" +
            "var getLoadTime = false;\n" +
            "\n" +
            "// performance.timing measurement used as 'starttime'\n" +
            "var startMeasure = \"fetchStart\";\n" +
            "\n" +
            "function contentHandler() {\n" +
            "  console.log(\"contentHandler\");\n" +
            "  // retrieve test settings from local ext storage\n" +
            "  if (typeof(browser) !== \"undefined\") {\n" +
            "    // firefox, returns promise\n" +
            "    browser.storage.local.get(\"settings\").then(function(item) {\n" +
            "      setup(item.settings);\n" +
            "    });\n" +
            "  } else {\n" +
            "    // chrome, no promise so use callback\n" +
            "    // chrome.storage.local.get(\"settings\", function(item) {\n" +
            "    //   setup(item.settings);\n" +
            "    // });\n" +
            // "    setup({type:'pageload', measure:{fcp: 1, }});\n" +
            " console.log('before setup');\n" +
            "    setup({type:'pageload', measure:{fcp: 1, fnbpaint: 1, dcf: 1, ttfi: 1, loadtime: 1}});\n" +
            " console.log('after setup');\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "function setup(settings) {\n" +
            "  if (settings.type != \"pageload\") {\n" +
            "    return;\n" +
            "  }\n" +
            "\n" +
            "  if (settings.measure == undefined) {\n" +
            "    console.log(\"abort: 'measure' key not found in test settings\");\n" +
            "    return;\n" +
            "  }\n" +
            "\n" +
            "  if (settings.measure.fnbpaint !== undefined) {\n" +
            "    getFNBPaint = settings.measure.fnbpaint;\n" +
            "    if (getFNBPaint) {\n" +
            "      console.log(\"will be measuring fnbpaint\");\n" +
            "      measureFNBPaint();\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
            "  if (settings.measure.dcf !== undefined) {\n" +
            "    getDCF = settings.measure.dcf;\n" +
            "    if (getDCF) {\n" +
            "      console.log(\"will be measuring dcf\");\n" +
            "      measureDCF();\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
            "  if (settings.measure.fcp !== undefined) {\n" +
            "    getFCP = settings.measure.fcp;\n" +
            "    if (getFCP) {\n" +
            "      console.log(\"will be measuring first-contentful-paint\");\n" +
            "      measureFCP();\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
            "  if (settings.measure.hero !== undefined) {\n" +
            "    if (settings.measure.hero.length !== 0) {\n" +
            "      getHero = true;\n" +
            "      heroesToCapture = settings.measure.hero;\n" +
            "      console.log(\"hero elements to measure: \" + heroesToCapture);\n" +
            "      measureHero();\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
            "  if (settings.measure.ttfi !== undefined) {\n" +
            "    getTTFI = settings.measure.ttfi;\n" +
            "    if (getTTFI) {\n" +
            "      console.log(\"will be measuring ttfi\");\n" +
            "      measureTTFI();\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
            "  if (settings.measure.loadtime !== undefined) {\n" +
            "    getLoadTime = settings.measure.loadtime;\n" +
            "    if (getLoadTime) {\n" +
            "      console.log(\"will be measuring loadtime\");\n" +
            "      measureLoadTime();\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "function measureHero() {\n" +
            "  var obs = null;\n" +
            "\n" +
            "  var heroElementsFound = window.document.querySelectorAll(\"[elementtiming]\");\n" +
            "  console.log(\"found \" + heroElementsFound.length + \" hero elements in the page\");\n" +
            "\n" +
            "  if (heroElementsFound) {\n" +
            "    function callbackHero(entries, observer) {\n" +
            "      entries.forEach(entry => {\n" +
            "        var heroFound = entry.target.getAttribute(\"elementtiming\");\n" +
            "        // mark the time now as when hero element received\n" +
            "        perfData.mark(heroFound);\n" +
            "        console.log(\"found hero:\" + heroFound);\n" +
            "        var resultType = \"hero:\" + heroFound;\n" +
            "        // calculcate result: performance.timing.fetchStart - time when we got hero element\n" +
            "        perfData.measure(name = resultType,\n" +
            "                         startMark = startMeasure,\n" +
            "                         endMark = heroFound);\n" +
            "        var perfResult = perfData.getEntriesByName(resultType);\n" +
            "        var _result = Math.round(perfResult[0].duration);\n" +
            "        sendResult(resultType, _result);\n" +
            "        perfData.clearMarks();\n" +
            "        perfData.clearMeasures();\n" +
            "        obs.disconnect();\n" +
            "      });\n" +
            "    }\n" +
            "    // we want the element 100% visible on the viewport\n" +
            "    var options = {root: null, rootMargin: \"0px\", threshold: [1]};\n" +
            "    try {\n" +
            "      obs = new window.IntersectionObserver(callbackHero, options);\n" +
            "      heroElementsFound.forEach(function(el) {\n" +
            "        // if hero element is one we want to measure, add it to the observer\n" +
            "        if (heroesToCapture.indexOf(el.getAttribute(\"elementtiming\")) > -1)\n" +
            "          obs.observe(el);\n" +
            "      });\n" +
            "    } catch (err) {\n" +
            "      console.log(err);\n" +
            "    }\n" +
            "  } else {\n" +
            "      console.log(\"couldn't find hero element\");\n" +
            "  }\n" +
            "\n" +
            "}\n" +
            "\n" +
            "function measureFNBPaint() {\n" +
            "  var x = window.performance.timing.timeToNonBlankPaint;\n" +
            "\n" +
            "  if (typeof(x) == \"undefined\") {\n" +
            "    console.log(\"ERROR: timeToNonBlankPaint is undefined; ensure the pref is enabled\");\n" +
            "    return;\n" +
            "  }\n" +
            "  if (x > 0) {\n" +
            "    console.log(\"got fnbpaint\");\n" +
            "    gRetryCounter = 0;\n" +
            "    var startTime = perfData.timing.fetchStart;\n" +
            "    sendResult(\"fnbpaint\", x - startTime);\n" +
            "  } else {\n" +
            "    gRetryCounter += 1;\n" +
            "    if (gRetryCounter <= 10) {\n" +
            "      console.log(\"\\nfnbpaint is not yet available (0), retry number \" + gRetryCounter + \"...\\n\");\n" +
            "      window.setTimeout(measureFNBPaint, 100);\n" +
            "    } else {\n" +
            "      console.log(\"\\nunable to get a value for fnbpaint after \" + gRetryCounter + \" retries\\n\");\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "function measureDCF() {\n" +
            "  var x = window.performance.timing.timeToDOMContentFlushed;\n" +
            "\n" +
            "  if (typeof(x) == \"undefined\") {\n" +
            "    console.log(\"ERROR: domContentFlushed is undefined; ensure the pref is enabled\");\n" +
            "    return;\n" +
            "  }\n" +
            "  if (x > 0) {\n" +
            "    console.log(\"got domContentFlushed: \" + x);\n" +
            "    gRetryCounter = 0;\n" +
            "    var startTime = perfData.timing.fetchStart;\n" +
            "    sendResult(\"dcf\", x - startTime);\n" +
            "  } else {\n" +
            "    gRetryCounter += 1;\n" +
            "    if (gRetryCounter <= 10) {\n" +
            "      console.log(\"\\dcf is not yet available (0), retry number \" + gRetryCounter + \"...\\n\");\n" +
            "      window.setTimeout(measureDCF, 100);\n" +
            "    } else {\n" +
            "      console.log(\"\\nunable to get a value for dcf after \" + gRetryCounter + \" retries\\n\");\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "function measureTTFI() {\n" +
            "  var x = window.performance.timing.timeToFirstInteractive;\n" +
            "\n" +
            "  if (typeof(x) == \"undefined\") {\n" +
            "    console.log(\"ERROR: timeToFirstInteractive is undefined; ensure the pref is enabled\");\n" +
            "    return;\n" +
            "  }\n" +
            "  if (x > 0) {\n" +
            "    console.log(\"got timeToFirstInteractive: \" + x);\n" +
            "    gRetryCounter = 0;\n" +
            "    var startTime = perfData.timing.fetchStart;\n" +
            "    sendResult(\"ttfi\", x - startTime);\n" +
            "  } else {\n" +
            "    gRetryCounter += 1;\n" +
            "    // NOTE: currently the gecko implementation doesn't look at network\n" +
            "    // requests, so this is closer to TimeToFirstInteractive than\n" +
            "    // TimeToInteractive.  TTFI/TTI requires running at least 5 seconds\n" +
            "    // past last \"busy\" point, give 25 seconds here (overall the harness\n" +
            "    // times out at 30 seconds).  Some pages will never get 5 seconds\n" +
            "    // without a busy period!\n" +
            "    if (gRetryCounter <= 25 * (1000 / 200)) {\n" +
            "      console.log(\"TTFI is not yet available (0), retry number \" + gRetryCounter + \"...\\n\");\n" +
            "      window.setTimeout(measureTTFI, 200);\n" +
            "    } else {\n" +
            "      // unable to get a value for TTFI - negative value will be filtered out later\n" +
            "      console.log(\"TTFI was not available for this pageload\");\n" +
            "      sendResult(\"ttfi\", -1);\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "var measureFCP = function() {\n" +
            "  console.log('measureFCP');\n" +
            "  // see https://developer.mozilla.org/en-US/docs/Web/API/PerformancePaintTiming\n" +
            "  var resultType = \"fcp\";\n" +
            "  var result;\n" +
            "\n" +
            "  // Firefox implementation of FCP is not yet spec-compliant (see Bug 1519410)\n" +
            "  result = window.performance.timing.timeToContentfulPaint;\n" +
            "  if (typeof(result) == \"undefined\") {\n" +
            "    // we're on chromium\n" +
            "    result = 0;\n" +
            "    let perfEntries = perfData.getEntriesByType(\"paint\");\n" +
            "console.log('length: ' + perfEntries.length);\n" +
            "\n" +
            "    if (perfEntries.length >= 2) {\n" +
            "      if (perfEntries[1].name == \"first-contentful-paint\" && perfEntries[1].startTime != undefined) {\n" +
            "        // this value is actually the final measurement / time to get the FCP event in MS\n" +
            "        result = perfEntries[1].startTime;\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
//            "console.log('timeout ID: ' + setTimeout(function() { console.log('timeout'); }, 100));\n" +
//            "\n" +
            "  if (result > 0) {\n" +
            "    console.log(\"got time to first-contentful-paint\");\n" +
            "    if (typeof(browser) !== \"undefined\") {\n" +
            "      // Firefox returns a timestamp, not the actual measurement in MS; need to calculate result\n" +
            "      var startTime = perfData.timing.fetchStart;\n" +
            "      result = result - startTime;\n" +
            "    }\n" +
            "    sendResult(resultType, result);\n" +
            "    perfData.clearMarks();\n" +
            "    perfData.clearMeasures();\n" +
            "  } else {\n" +
            "    gRetryCounter += 1;\n" +
            "    if (gRetryCounter <= 10) {\n" +
            "      console.log(\"\\ntime to first-contentful-paint is not yet available (0), retry number \" + gRetryCounter + \"...\\n\");\n" +
            "      window.setTimeout(measureFCP, 100);\n" +
            "    } else {\n" +
            "      console.log(\"\\nunable to get a value for time-to-fcp after \" + gRetryCounter + \" retries\\n\");\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "function measureLoadTime() {\n" +
            "  var x = window.performance.timing.loadEventStart;\n" +
            "\n" +
            "  if (typeof(x) == \"undefined\") {\n" +
            "    console.log(\"ERROR: loadEventStart is undefined\");\n" +
            "    return;\n" +
            "  }\n" +
            "  if (x > 0) {\n" +
            "    console.log(\"got loadEventStart: \" + x);\n" +
            "    gRetryCounter = 0;\n" +
            "    var startTime = perfData.timing.fetchStart;\n" +
            "    sendResult(\"loadtime\", x - startTime);\n" +
            "  } else {\n" +
            "    gRetryCounter += 1;\n" +
            "    if (gRetryCounter <= 40 * (1000 / 200)) {\n" +
            "      console.log(\"\\loadEventStart is not yet available (0), retry number \" + gRetryCounter + \"...\\n\");\n" +
            "      window.setTimeout(measureLoadTime, 100);\n" +
            "    } else {\n" +
            "      console.log(\"\\nunable to get a value for loadEventStart after \" + gRetryCounter + \" retries\\n\");\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "function sendResult(_type, _value) {\n" +
            "  // send result back to background runner script\n" +
            "  console.log(\"sending result back to runner: \" + _type + \" \" + _value);\n" +
//            "  chrome.runtime.sendMessage({\"type\": _type, \"value\": _value}, function(response) {\n" +
//            "    if (response !== undefined) {\n" +
//            "      console.log(response.text);\n" +
//            "    }\n" +
//            "  });\n" +
            "}\n" +
            "\n" +
            "console.log(\"YYY YYY\");\n" +
            "window.onload = contentHandler();\n";

//    public static final String BIG_STRING2 =
//            "console.log('2before setting onload');" +
//                    "window.onload = function() { console.log('2before setting interval'); };" +
//                    "window.addEventListener('load', function() { console.log('2addeventlistener'); });" +
//                    "window.addEventListener('DOMContentLoaded', function() { console.log('2DOMContent'); });" +
//                    "setInterval(function() { console.log('2interval'); }, 10);" +
//                    "console.log('2after setting onload');" +
//                    "";
    // window.setInterval(function() { console.log('interval') }, 20);
}
