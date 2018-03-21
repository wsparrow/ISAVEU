var chartVars = "rMateOnLoadCallFunction=chartReadyHandler";
rMateChartH5.create("chart1", "chartHolder", chartVars, "100%", "100%"); 
function chartReadyHandler(id) {
 document.getElementById(id).setLayout(layoutStr);
   document.getElementById(id).setData(chartData);
}
var layoutStr = 
             '<rMateChart frameColor="#EEEEEE"  borderStyle="none">'
                   +'<Options>'
                      +'<Caption text="게이지 - Gradient" fontFamily="맑은 고딕"/>'
                    +'</Options>'
                 +'<NumberFormatter id="numFmt" precision="0"/>'
                  +'<CircularGauge valueChangeFunction="valueChangeFunc" padding="8" startAngle="90" minimumAngle="40" maximumAngle="320" minimum="0" maximum="500" value="200" interval="50" minorInterval="10" formatter="{numFmt}" tickLabelStyleName="tickText" valueLabelStyleName="valueText" editMode="true" majorTickType="circle" liveDragging="false" tickGap="-3" labelGap="-5" showDataTip="false" tickLabelPlacement="outside" tickColor="#1B699A" needleCoverRadius="100" needleThickness="100" pointThickness="0" tickRadius="2" needleLengthRatio="0.9" minorTickType="line" minorTickRadius="5" needlePointStyle="rounding" isValueTop="true" animationDuration="1000" bounceAnimating="true" rotateTickLabel="true" showOutFrame="true" outFrameLabelXOffset="0" outFrameLabelYOffset="0.2">'
                     +'<frameFill>'
                            +'<LinearGradient angle="0">'
                             +'<entries>'
                                  +'<GradientEntry color="#f6af43" ratio="0"/>'
                                 +'<GradientEntry color="#f55a58" ratio="1"/>'
                             +'</entries>'
                         +'</LinearGradient>'
                      +'</frameFill>'
                       +'<frameStroke>'
                          +'<Stroke color="#ffffff" weight="0"/>'
                       +'</frameStroke>'
                     +'<needleFill>'
                           +'<SolidColor color="#ffffff"/>'
                      +'</needleFill>'
                      +'<needleStroke>'
                         +'<Stroke color="#ffffff"/>'
                      +'</needleStroke>'
                        +'<needleCoverFill>'
                          +'<SolidColor color="#ffffff"/>'
                      +'</needleCoverFill>'
                     +'<needleCoverStroke>'
                            +'<Stroke color="#ffffff"/>'
                      +'</needleCoverStroke>'
                       +'<minorTickStroke>'
                          +'<Stroke color="#ffffff" alpha="0.5"/>'
                      +'</minorTickStroke>'
                     +'<tickFill>'
                         +'<SolidColor color="#ffffff"/>'
                      +'</tickFill>'
                        +'<tickStroke>'
                           +'<Stroke color="#ffffff" weight="3"/>'
                       +'</tickStroke>'
                  +'</CircularGauge>'
                   +'<Style>'
                        +'.valueText{'
                          +'fontSize:35;'
                         +'textAlign:center;'
                            +'backgroundColor:#FFFFFF;'
                         +'backgroundAlpha:0;'
                           +'paddingTop:0;'
                            +'borderStyle:none;'
                            +'fontWeight:bold;'
                         +'color:#555555;'
                       +'}'
                        +'.tickText{'
                           +'fontFamily:"맑은 고딕";'
                          +'fontSize:11;'
                         +'color:#555555;'
                       +'}'
                    +'</Style>'
               +'</rMateChart>';
var chartData = [201];
function changeValue()
{
 document.getElementById("chart1").setData([parseInt(Math.random()*100) * 5]);
}
var gaugeValue;
function valueChangeFunc(value)
{
    gaugeValue = value;
}
function commitValue()
{
    alert(gaugeValue);
}
rMateChartH5.registerTheme(rMateChartH5.themes);
function rMateChartH5ChangeTheme(theme){
    document.getElementById("chart1").setTheme(theme);
}