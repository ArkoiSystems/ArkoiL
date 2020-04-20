import React from "react";

import { Container, Row, Col } from "react-bootstrap";
import { Link } from "react-router-dom";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faFacebookF,
  faTwitter,
  faLinkedinIn,
  faGithub,
} from "@fortawesome/free-brands-svg-icons";

import "../styles/Footer.css";

class Footer extends React.Component {
  render() {
    return (
      <footer>
        <Container className="text-center">
          <Row>
            <this.Links />
            <this.Icons />
          </Row>
          <Col xs="auto" className="footer-copyright">
            <p>Copyright 2019-2020 &copy; Arkoi Systems</p>
          </Col>
        </Container>
      </footer>
    );
  }

  Links() {
    return (
      <Col xs="12" sm="6">
        <Row>
          <Col md="auto">
            <Link to="/cookies" className="footer-item">
              Cookies
            </Link>
          </Col>
          <Col md="auto">
            <Link to="/support" className="footer-item">
              Support
            </Link>
          </Col>
          <Col md="auto">
            <Link to="/contact" className="footer-item">
              Contact
            </Link>
          </Col>
          <Col md="auto">
            <Link to="/privacy" className="footer-item">
              Privacy Policy
            </Link>
          </Col>
          <Col md="auto">
            <Link to="/terms" className="footer-item">
              Terms & Conditions
            </Link>
          </Col>
        </Row>
      </Col>
    );
  }

  Icons() {
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
}

export default Footer;
