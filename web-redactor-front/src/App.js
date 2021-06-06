import React, {Component} from "react";

import {Switch, Route, Link} from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import './App.css';

import Uploader from "./components/uploader.component";


class App extends Component {
    constructor(props) {
        super(props);

        this.state = {
            currentUser: undefined
        };
    }

    render() {
        return (

            <div className="App">
                <nav className="navbar navbar-expand color-dark-blue">
                    <div className="navbar-nav mr-auto">
                        <li className="nav-item">
                            <Link to={"/image-merge"} className="nav-link color-dark-blue">
                                Merge your images
                            </Link>
                        </li>
                    </div>

                </nav>
                <div className="container mt-3">
                    <Switch>
                        <Route exact path="/image-merge" component={Uploader}/>
                    </Switch>
                </div>
            </div>
        );
    }
}

export default App;
