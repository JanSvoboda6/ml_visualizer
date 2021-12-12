import React, { useState } from "react";
import { Link, Redirect } from 'react-router-dom';
import { connect } from "react-redux";
import logo from '../../styles/logo_but_text.png';
import dots from '../../styles/dots_logo_big.svg';
import RegisterService from "../../services/RegisterService";
import FadeIn from "react-fade-in";

function Register()
{
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [isRegistrationSuccessful, setRegistrationSuccessful] = useState(false);

    const onChangeUsername = (e: { target: { value: string; }; }) =>
    {
        setUsername(e.target.value);
    }

    const onChangePassword = (e: { target: { value: string; }; }) =>
    {
        setPassword(e.target.value);
    }

    const handleRegister = (e: { preventDefault: () => void; }) =>
    {
        e.preventDefault();
        setRegistrationSuccessful(false);

        var isValidationSuccesful = true;
        if (isValidationSuccesful) //TODO Jan: implement proper validation
        {
            RegisterService(username, password).then(
                () =>
                {
                    setRegistrationSuccessful(true)
                },
                (error: any) =>
                {
                    var message = "";
                    if (error && error.response && error.response.data.message)
                    {
                        message = error.response.data.message;
                    }
                    else if (error.message)
                    {
                        console.log(error.message)
                        message = error.message;
                    }
                    else if (error.toString())
                    {
                        message = error.toString();
                    }

                    setMessage(message)
                });
        };
    }

    if (isRegistrationSuccessful)
    {
        return <Redirect to="/login?popup=t" />;
    }

    return (
        <div>
            {/* <div className="wrapper">
                <a className="register-item logo-register"><img className='logo-dots-bigger' src={dots} alt="logo_dots" /></a>
            </div> */}
            <FadeIn>
                <div className="register-page">
                    <img className='logo' src={logo} alt="logo_but" />
                    <form onSubmit={handleRegister}>
                        <div>
                            <div className="register-item email-text">
                                <input
                                    type="text"
                                    className="input-text"
                                    name="email"
                                    placeholder="Email"
                                    value={username}
                                    onChange={onChangeUsername}
                                />
                            </div>
                            <div className="register-item password-text">
                                <input
                                    type="password"
                                    className="input-text"
                                    name="password"
                                    placeholder="Password"
                                    value={password}
                                    onChange={onChangePassword}
                                />
                            </div>
                            <div className="register-item">
                            <button className="submit-button">Sign Up</button>
                            </div>
                        </div>
                        {message !== "" && (
                            <div className="register-item">
                                <div className="alert-text">
                                {message}
                                </div>
                            </div>
                        )}
                    </form>
                    <div className="register-link">
                        <p className="register-link-text" >Already have an account?</p>
                        <Link className="register-link-reference" to="/login">Login</Link>
                    </div>
                </div>
            </FadeIn >
        </div>
    );
}


export default connect()(Register);