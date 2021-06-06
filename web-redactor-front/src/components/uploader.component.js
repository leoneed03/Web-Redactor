import React, {Component} from 'react';
import axios from 'axios';
import {ToastContainer, toast} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const API_URL = process.env.REACT_APP_SERVER_URL + '/api/'

class Uploader extends Component {
    constructor(props) {
        super(props);
        this.download = this.download.bind(this);
        this.downloadMergedUpDown = this.downloadMergedUpDown.bind(this);

        this.state = {
            prevIc: null,
            lastId: null,
            selectedFile: null,
            selectedIds: null
        }
    }

    uploadAttachment(file) {
        console.log("Got file to upload");
        let formData = new FormData();

        formData.append("file", file);

        console.log(file)

        return axios.post(API_URL + 'upload', formData, {
            headers: {'Content-Type': 'multipart/form-data'},
        });
    }


    checkMimeType = (event) => {
        let files = event.target.files
        let err = []
        const types = ['image/png', 'image/jpeg', 'image/gif']

        for (var x = 0; x < files.length; x++) {

            if (types.every(type => files[x].type !== type)) {
                err[x] = files[x].type + ' is not a supported format\n';
            }
        }

        for (var z = 0; z < err.length; z++) {
            toast.error(err[z])
            event.target.value = null
        }
        return true;
    }

    onChangeHandler = event => {
        var files = event.target.files

        if (this.checkMimeType(event)) {
            this.setState({
                selectedFile: files
            })
        }
    }
    fileId;
    onClickHandler = () => {
        const data = new FormData()
        if (this.state.selectedFile === null) {
            return
        }
        this.uploadAttachment(this.state.selectedFile[0]).then(response => {
            this.setState((prev) => {
                console.log("Got id ", response.data.fileId);
                return {
                    prevId: prev.lastId,
                    lastId: response.data.fileId,
                }
            })
            console.log(response)
        })
    }

    async downloadAttachment(fileId) {
        axios.get(API_URL + 'download/' + fileId, {responseType: 'blob'})
            .then(response => {
                var fileDownload = require('js-file-download');
                let fileName = 'download.png'
                fileDownload(response.data, fileName);

                return response;
            });
    }

    download(fileId, initialFileName) {
        this.downloadAttachment(fileId);
    }

    downloadGrayscale(fileId) {
        this.downloadAttachmentGrayscale(fileId);
    }

    async downloadAttachmentGrayscale(fileId) {
        axios.get(API_URL + 'to-grayscale/' + fileId, {responseType: 'blob'})
            .then(response => {
                var fileDownload = require('js-file-download');
                let fileName = 'to-grayscale.png'
                fileDownload(response.data, fileName);
                console.log(response)
                return response;
            })
    }

    async downloadMergedUpDown(fileIdUp, fileIdDown) {
        axios.get(API_URL + 'merge/' + fileIdUp + '&' + fileIdDown, {responseType: 'blob'})
            .then(response => {
                var fileDownload = require('js-file-download');
                let fileName = 'merged.png'
                fileDownload(response.data, fileName);
                console.log(response)
                return response;

            })
    }

    downloadMergedUpdown(fileIdUp, fileIdDown) {
        this.downloadMergedUpDown(fileIdUp, fileIdDown);
    }

    render() {
        return (
            <div className="container">
                <div className="row">
                    <div className="offset-md-3 col-md-6">
                        <div className="form-group files">
                            <label>Upload Your File </label>
                            <input type="file" className="form-control" multiple onChange={this.onChangeHandler}/>
                        </div>

                        <button type="button" className="btn btn-success btn-block" onClick={this.onClickHandler}>Upload
                        </button>

                        <button
                            className="btn btn-primary btn-block color-dark-blue"
                            onClick={() => this.download(this.state.lastId)}>Download
                        </button>
                        <button
                            className="btn btn-primary btn-block color-dark-blue"
                            onClick={() => this.downloadGrayscale(this.state.lastId)}>Download Grayscale
                        </button>

                        <button
                            className="btn btn-primary btn-block color-dark-blue"
                            onClick={() => this.downloadMergedUpdown(this.state.lastId, this.state.prevId)}>Merge 2 last uploaded images
                        </button>
                    </div>
                </div>
            </div>
        );
    }
}

export default Uploader;