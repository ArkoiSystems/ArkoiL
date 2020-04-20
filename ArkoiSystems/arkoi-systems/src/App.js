import React from "react";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";

import {
  Cookies,
  About,
  Projects,
  Blog,
  Contact,
  Support,
  Privacy,
  Terms,
  Home,
} from "./pages";
import { NavigationBar, Footer } from "./components";

import "./styles/App.css";

class App extends React.Component {
  render() {
    return (
      <Router>
        <NavigationBar />
        <Switch>
          <Route path="/cookies">
            <Cookies />
          </Route>
          <Route path="/about">
            <About />
          </Route>
          <Route path="/projects">
            <Projects />
          </Route>
          <Route path="/blog">
            <Blog />
          </Route>
          <Route path="/contact">
            <Contact />
          </Route>
          <Route path="/support">
            <Support />
          </Route>
          <Route path="/privacy">
            <Privacy />
          </Route>
          <Route path="/terms">
            <Terms />
          </Route>
          <Route path="*">
            <Home />
          </Route>
        </Switch>
        <Footer />
      </Router>
    );
  }
}

export default App;
