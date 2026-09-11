import { Navbar } from './components/Navbar' 
import { Hero } from './components/Hero' 
import { AppVideoShowcase } from './components/AppVideoShowcase' 
import { Features } from './components/Features' 
import { InteractiveAppExperience } from './components/InteractiveAppExperience' 
import { QuickStart } from './components/QuickStart' 
import { Footer } from './components/Footer' 

export function App() {
  return (
    <div className="min-h-screen bg-[#080C14] text-[#E2E8F0] selection:bg-pink-500/20 selection:text-pink-200"> 
      <Navbar /> 
      <main> 
        <Hero /> 
        <AppVideoShowcase /> 
        <Features /> 
        <InteractiveAppExperience /> 
        <QuickStart /> 
      </main> 
      <Footer /> 
    </div> 
  ) 
} 

export default App
