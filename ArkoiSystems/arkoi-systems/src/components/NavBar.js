import React from "react";

import { Navbar, Nav } from "react-bootstrap";
import { Link } from "react-router-dom";

import logo from "../assets/resources/logo.png";
import "../styles/NavBar.css";

class NavigationBar extends React.Component {
  render() {
    return (
      <Navbar expand="lg" fixed="top" variant="dark">
        <Link to="/">
          <Navbar.Brand href="#">
            <img
              src={logo}
              height="34"
              className="d-inline-block align-top"
              alt="Arkoi Systems"
            />
          </Navbar.Brand>
        </Link>
        <Navbar.Toggle />
        <Navbar.Collapse>
          <Nav.Item className="ml-auto">
            <Link to="/about">About</Link>
          </Nav.Item>
          <Nav.Item>
            <Link to="/projects">Projects</Link>
          </Nav.Item>
          <Nav.Item>
            <Link to="/blog">Blog</Link>
          </Nav.Item>
          <Nav.Item>
            <Link to="/contact">Contact</Link>
          </Nav.Item>
        </Navbar.Collapse>
      </Navbar>
    );
  }
}

export default NavigationBar;
