import React from "react";

import ReactDOM from "react-dom";

import { Navbar, Nav, Container, Row, Col } from "react-bootstrap";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faFacebookF,
  faTwitter,
  faLinkedinIn,
  faGithub,
} from "@fortawesome/free-brands-svg-icons";

import logo from "./logo.png";
import "./index.css";

function NavigationBar() {
  return (
    <Navbar expand="lg" fixed="top" variant="dark">
      <Navbar.Brand href="#">
        <img
          src={logo}
          height="34"
          className="d-inline-block align-top"
          alt="Arkoi Systems"
        />
      </Navbar.Brand>
      <Navbar.Toggle />
      <Navbar.Collapse>
        <Nav.Item className="ml-auto">
          <Nav.Link href="#about">About</Nav.Link>
        </Nav.Item>
        <Nav.Item>
          <Nav.Link href="#projects">Projects</Nav.Link>
        </Nav.Item>
        <Nav.Item>
          <Nav.Link href="#blog">Blog</Nav.Link>
        </Nav.Item>
        <Nav.Item>
          <Nav.Link href="#contact">Contact</Nav.Link>
        </Nav.Item>
      </Navbar.Collapse>
    </Navbar>
  );
}

function Development() {
  return (
    <div style={{ height: "100vh" }}>
      <Container className="h-100 text-center">
        <Row className="h-100 justify-content-center align-items-center">
          <Col xs="12">
            <h1>Under development.</h1>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

function Footer() {
  return (
    <footer>
      <Container className="text-center">
        <Row>
          <FooterLinks />
          <SocialIcons />
        </Row>
        <Col xs="auto" className="footer-copyright">
          <p>Copyright 2019-2020 &copy; Arkoi Systems</p>
        </Col>
      </Container>
    </footer>
  );
}

function FooterLinks() {
  return (
    <Col xs="12" sm="6">
      <Row>
        <Col md="auto">
          <a className="footer-item" href="#">
            Cookies
          </a>
        </Col>
        <Col md="auto">
          <a className="footer-item" href="#">
            Support
          </a>
        </Col>
        <Col md="auto">
          <a className="footer-item" href="#">
            Contact
          </a>
        </Col>
        <Col md="auto">
          <a className="footer-item" href="#">
            Privacy Policy
          </a>
        </Col>
        <Col md="auto">
          <a className="footer-item" href="#">
            Terms & Conditions
          </a>
        </Col>
      </Row>
    </Col>
  );
}

function SocialIcons() {
  return (
    <Col xs="12" sm="6" className="d-inline align-middle">
      <Row className="justify-content-center">
        <a className="socialButton" href="#">
          <FontAwesomeIcon icon={faFacebookF} />
        </a>
        <a className="socialButton" href="#">
          <FontAwesomeIcon icon={faTwitter} />
        </a>
        <a className="socialButton" href="#">
          <FontAwesomeIcon icon={faLinkedinIn} />
        </a>
        <a className="socialButton" href="https://github.com/ArkoiSystems">
          <FontAwesomeIcon icon={faGithub} />
        </a>
      </Row>
    </Col>
  );
}

function App() {
  return (
    <div>
      <NavigationBar />
      <Development />
      <Footer />
    </div>
  );
}

ReactDOM.render(<App />, document.getElementById("root"));
