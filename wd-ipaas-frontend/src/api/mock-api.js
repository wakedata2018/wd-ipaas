import axios from 'axios';

export default {
  getMockData(type){
    return axios.get(`/mock-${type}`).then(response=>{
      return response
    })
  }
}

