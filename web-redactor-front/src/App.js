import logo from './logo.svg';
import './App.css';

// import Home from "./components/home.component";
import Uploader from "./components/uploader.component";

function App() {
  return (
      <div className="App">
        <Uploader/>
        <p>
          Welcome to image uploader [App]!
        </p>
      </div>
  );
}

export default App;
