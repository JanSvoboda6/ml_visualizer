import React from "react";
import Navbar from "./Navbar";
import PieChart from "./PieChart";
import Graph from "./Graph";

function Summary()
{
    return (
        <div>
            <Navbar start="start-at-summary" />
            <div className="summary-list">
                <div className="summary-list-item">
                    <div className="total-list">
                        <div className="total">Total Projects Created: <div className="total-number">3</div></div>
                        <div className="total">Total Training Runs: <div className="total-number">11</div></div>
                        <div className="total">Total Validation Runs: <div className="total-number">11</div></div>
                    </div>
                </div>
                <div className="summary-list-item"> <PieChart /></div>
                <div className="summary-list-item"> <Graph backgroundColor='rgba(229, 81, 116, 0.9)' /></div>
            </div>
        </div>
    )
}

export default Summary;