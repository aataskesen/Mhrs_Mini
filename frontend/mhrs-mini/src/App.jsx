import { useState } from 'react'
import './App.css'

// Props alan Header component'i
function Header({ title, subtitle }) {
  return (
    <header>
      <h1>{title}</h1>
      <p>{subtitle}</p>
    </header>
  )
}

// Props alan Button component'i
function Button({ text, onClick, color = "blue" }) {
  return (
    <button 
      onClick={onClick}
      style={{ 
        backgroundColor: color, 
        color: "white", 
        padding: "10px 20px",
        margin: "5px",
        border: "none",
        borderRadius: "5px"
      }}
    >
      {text}
    </button>
  )
}

// State kullanarak Counter component'i
function Counter() {
  const [count, setCount] = useState(0)
  
  return (
    <div className="counter">
      <h2>Sayaç: {count}</h2>
      <Button 
        text="Artır (+)" 
        onClick={() => setCount(count + 1)} 
        color="green"
      />
      <Button 
        text="Azalt (-)" 
        onClick={() => setCount(count - 1)} 
        color="red"
      />
      <Button 
        text="Sıfırla" 
        onClick={() => setCount(0)} 
        color="gray"
      />
    </div>
  )
}

// Ana App component'i
function App() {
  return (
    <div className="app">
      <Header />
      <Counter />
      <footer>
        <p>React ile component yapısını öğreniyoruz!</p>
      </footer>
    </div>
  )
}

export default App
