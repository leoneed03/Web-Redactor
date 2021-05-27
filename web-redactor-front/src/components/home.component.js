import React, {Component} from 'react';
import logo from '../logo.svg';
import '../App.css';

import AttachmentService from "../services/attachment.service";

class Home extends Component {
    uploadFiles() {
        const selectedFiles = this.state.selectedFiles;

        let _progressInfos = [];

        for (let i = 0; i < selectedFiles.length; i++) {
            _progressInfos.push({percentage: 0, fileName: selectedFiles[i].name});
        }

        this.setState(
            {
                progressInfos: _progressInfos,
                message: [],
            },
            () => {
                for (let i = 0; i < selectedFiles.length; i++) {
                    this.upload(i, selectedFiles[i]);
                }
            }
        );
    }

    async upload(idx, file) {
        let _progressInfos = [...this.state.progressInfos];

        let isDicom = file.name.includes(".dcm");


        let toSend = file;

        AttachmentService.uploadAttachment(toSend, file.name, isDicom,
            (event) => {
                _progressInfos[idx].percentage = Math.round((100 * event.loaded) / event.total);
                this.setState({
                    _progressInfos,
                });
            })
            .then((response) => {
                this.setState((prev) => {
                    let nextMessage = [...prev.message, "Успешно загружен файл: " + file.name];
                    return {message: nextMessage};
                });

                // return AttachmentService.getAttachmentsInfoForUser(_currentUser.username);
            })
            .then((files) => {
                this.setState({fileInfos: files.data,});
            })
            .catch(() => {
                _progressInfos[idx].percentage = 0;
                this.setState((prev) => {
                    let nextMessage = [...prev.message, "Не удалось загрузить файл: " + file.name];
                    return {
                        progressInfos: _progressInfos,
                        message: nextMessage
                    };
                });
            });
    }


    // fileUploadPage(){
    //     return(
    //         <div>
    //             <input type="file" name="file" onChange={changeHandler} />
    //             <div>
    //                 <button onClick={handleSubmission}>Submit</button>
    //             </div>
    //         </div>
    //     )
    // }



    render() {

        return (
            // <div className="col-3">
            //     <button
            //         className="btn btn-primary btn-block color-middle-blue"
            //         onClick={this.uploadFiles}
            //     >Загрузить</button>
            // </div>


            // <div><input type="file" name="file" onChange={changeHandler} />
            //     <div>
            //         <button onClick={handleSubmission}>Submit</button>
            //     </div></div>

            <div>HomePlace!</div>

            // <div className="App">
            //     <header className="App-header">
            //         <img src={logo} className="App-logo" alt="logo" />
            //         <div className="App-intro">
            //             <h2>JUG List</h2>
            //
            //         </div>
            //     </header>
            // </div>
        );
    }
}

export default Home;