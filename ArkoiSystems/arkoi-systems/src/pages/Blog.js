import React from "react";
import { Container, Row, Col } from "react-bootstrap";

class Blog extends React.Component {
  render() {
    return (
      <div style={{ height: "100vh" }}>
        <Container className="h-100 text-center">
          <Row className="h-100 justify-content-center align-items-center">
            <Col xs="12">
              <h1>Blog</h1>
            </Col>
          </Row>
        </Container>
      </div>
    );
  }
}

export default Blog;
