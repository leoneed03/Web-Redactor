import React, {Component} from 'react';
import axios from 'axios';
import {Progress} from 'reactstrap';
import {ToastContainer, toast} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

class Visualizer extends Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedIds: null,
        }

    }

    visualizeAttachment(id) {
        console.log("Got file to visualize " + id);
        let formData = new FormData();

        formData.append("file id: ", id);

        console.log(formData)

        return axios.post("http://localhost:7999/download", formData, {
            headers: {'Content-Type': 'multipart/form-data'},
        });
    }

    onClickHandlerVisualize = () => {
        const data = new FormData()
        if (this.state.selectedFile === null) {
            return
        }
        this.visualizeAttachment(this.state.selectedFile[0], '').then(response => {
            console.log(response)
        })
    }

    render() {
        return (
            <div class="container">
                <div class="row">
                    <div class="offset-md-3 col-md-6">
                        <div class="form-group files">
                            <label>Upload Your File </label>
                            <input type="file" class="form-control" multiple onChange={this.onChangeHandler}/>
                        </div>
                        <div class="form-group">
                            <ToastContainer/>
                            <Progress max="100" color="success"
                                      value={this.state.loaded}>{Math.round(this.state.loaded, 2)}%</Progress>

                        </div>

                        <button type="button" class="btn btn-success btn-block" onClick={this.onClickHandlerVisualize}>Upload
                        </button>

                    </div>
                </div>
            </div>
        );
    }
}

export default Uploader;