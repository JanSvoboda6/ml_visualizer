import React, { Component } from 'react';
import axios from "axios";

import loadingIcon from './styles/loading_icon.svg'
const API_URL = "http://localhost:8080/api";

class ModelList extends Component
{
  constructor(props)
  {
    super(props);
    this.state = {
      error: null,
      isLoaded: false,
      models: []
    };
  }

  componentDidMount()
  {
    axios.get(API_URL + "/models")
      .then(
        (res) =>
        {
          console.log(res.data)
          this.setState({
            isLoaded: true,
            models: res.data
          });
        },
        (error) =>
        {
          this.setState({
            isLoaded: true,
            error
          });
        }
      )
  }

  render()
  {
    const { error, isLoaded, models: models } = this.state;
    if (error)
    {
      return <div>Error: { error.message }</div>;
    } else if (!isLoaded)
    {
      return <div className="project-loading-message"><img className='loading-icon' src={ loadingIcon } alt="loading_icon" /></div>;
    } else
    {
      return (
        <ul className="project-list">
          { models.map(model => (
            <li key={ model.id } className="project-item">
              { model.id } &nbsp;&nbsp; { model.name }
            </li>
          )) }
        </ul>
      );
    }
  }
}

export default ModelList;