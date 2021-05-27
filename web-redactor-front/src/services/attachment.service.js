import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL + '/api/files/';

class AttachmentService {

    uploadAttachment(file, fileName, isDicom, onUploadProgress) {
        console.log("Got file to upload");
        let formData = new FormData();

        if (isDicom) {
            formData.append("file", new Blob([file]), fileName);
        } else {
            formData.append("file", file);
        }

        const user = JSON.parse(localStorage.getItem('user'));
        let token = '';
        if (user && user.token) {
            token = user.token;
        }
        return axios.post(API_URL + "upload", formData, {
            headers: {'Content-Type': 'multipart/form-data', 'Authorization': 'Bearer ' + token},
            onUploadProgress,
        });
    }
}

export default new AttachmentService();