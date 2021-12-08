import React from 'react';
import { Group } from '@visx/group';
import genBins, { Bin, Bins } from '@visx/mock-data/lib/generators/genBins';
import { scaleLinear } from '@visx/scale';
import { HeatmapCircle, HeatmapRect } from '@visx/heatmap';
import { getSeededRandom } from '@visx/mock-data';


// const hot1 = '#77312f';
// const hot2 = '#f33d15';
const hot1 = '#122549';
const hot2 = '#b4fbde';
export const background = '#221c1c';

const binData = genBins(/* length = */ 15, /* height = */ 15);

function max<Datum>(data: Datum[], value: (d: Datum) => number): number
{
    return Math.max(...data.map(value));
}

function min<Datum>(data: Datum[], value: (d: Datum) => number): number
{
    return Math.min(...data.map(value));
}

// accessors
const bins = (d: Bins) => d.bins;
const count = (d: Bin) => d.count;

const colorMax = max(binData, d => max(bins(d), count));
const bucketSizeMax = max(binData, d => bins(d).length);

// scales
const xScale = scaleLinear < number > ({
    domain: [0, binData.length],
});
const yScale = scaleLinear < number > ({
    domain: [0, bucketSizeMax],
});
const circleColorScale = scaleLinear < string > ({
    range: [hot1, hot2],
    domain: [0, colorMax],
});
const rectColorScale = scaleLinear < string > ({
    range: [hot1, hot2],
    domain: [0, colorMax],
});
const opacityScale = scaleLinear < number > ({
    range: [1, 1],
    domain: [0, colorMax],
});

export type HeatmapProps = {
    width: number;
    height: number;
    margin?: { top: number; right: number; bottom: number; left: number };
    separation?: number;
    events?: boolean;
};

const defaultMargin = { top: 10, left: 20, right: 20, bottom: 110 };

function changeBackground(e)
{
    e.target.style.maring = '10px';
}

export default ({
    width,
    height,
    events = false,
    margin = defaultMargin,
    separation = 20,
}: HeatmapProps) =>
{
    // bounds
    const size =
        width > margin.left + margin.right ? width - margin.left - margin.right - separation : width;
    const xMax = size / 2;
    const yMax = height - margin.bottom - margin.top;

    const binWidth = xMax / binData.length;
    const binHeight = yMax / bucketSizeMax;
    const radius = min([binWidth, binHeight], d => d) / 2;

    xScale.range([0, xMax]);
    yScale.range([yMax, 0]);

    return width < 10 ? null : (
        <svg width={ width/2 } height={ height }>
                <HeatmapRect
                    data={ binData }
                    xScale={ xScale }
                    yScale={ yScale }
                    colorScale={ rectColorScale }
                    opacityScale={ opacityScale }
                    binWidth={ binWidth }
                    binHeight={ binWidth }
                    gap={ 2 }
                >
                    { heatmap =>
                        heatmap.map(heatmapBins =>
                            heatmapBins.map(bin => (
                                <rect
                                    key={ `heatmap-rect-${ bin.row }-${ bin.column }` }
                                    className="vx-heatmap-rect heatmap-bin"
                                    width={ bin.width }
                                    height={ bin.height }
                                    x={ bin.x }
                                    y={ bin.y }
                                    fill={ bin.color }
                                    fillOpacity={ bin.opacity }
                                    onMouseEnter={changeBackground}
                                    onClick={ () =>
                                    {
                                        //if (!events) return;
                                        const { row, column } = bin;
                                        console.log(JSON.stringify({ row, column, bin: bin.bin }));
                                    } }
                                />
                            )),
                        )
                    }
                </HeatmapRect>
        </svg>
    );
};