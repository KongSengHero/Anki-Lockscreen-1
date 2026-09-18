import React, { useState, useEffect, useRef } from 'react' 
import { UploadCloud, FolderOpen, Play, CheckCircle2, AlertCircle, Plus, BookOpen } from 'lucide-react' 
import type { AnkiDeck } from '../../types/anki' 
import { getDecks, getDueCards } from '../../services/db' 
import { parseAnkiDeck } from '../../services/ankiParser' 

interface DeckListProps {
  onSelectDeck: (deckId?: number) => void 
} 

export const DeckList: React.FC<DeckListProps> = ({ onSelectDeck }) => {
  const [decks, setDecks] = useState<AnkiDeck[]>([]) 
  const [totalDue, setTotalDue] = useState(0) 
  const [isImporting, setIsImporting] = useState(false) 
  const [importStatus, setImportStatus] = useState<string | null>(null) 
  const [errorMessage, setErrorMessage] = useState<string | null>(null) 
  const fileInputRef = useRef<HTMLInputElement | null>(null) 

  const loadDecks = async () => {
    const list = await getDecks() 
    setDecks(list) 
    const allDue = await getDueCards() 
    setTotalDue(allDue.length) 
  } 

  useEffect(() => {
    loadDecks() 
  }, []) 

  const handleFileUpload = async (file: File) => {
    if (!file.name.match(/\.(apkg|colpkg)$/i)) {
      setErrorMessage('Please select a valid Anki .apkg or .colpkg file') 
      return 
    } 

    setIsImporting(true) 
    setErrorMessage(null) 
    setImportStatus('Extracting archive and parsing Anki SQLite database...') 

    try {
      const res = await parseAnkiDeck(file) 
      setImportStatus(`Successfully imported "${res.deckName}" (${res.cardCount} cards)!`) 
      await loadDecks() 
      setTimeout(() => setImportStatus(null), 4000) 
    } catch (err: any) {
      setErrorMessage(err?.message || 'Failed to import Anki deck') 
    } finally {
      setIsImporting(false) 
    } 
  } 

  const onDragOver = (e: React.DragEvent) => {
    e.preventDefault() 
  } 

  const onDrop = (e: React.DragEvent) => {
    e.preventDefault() 
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFileUpload(e.dataTransfer.files[0]) 
    } 
  } 

  return (
    <div className="max-w-xl mx-auto px-4 pt-6 pb-28">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight">
            Anki Decks
          </h2> 
          <p className="text-xs text-slate-400 mt-0.5">
            Offline Spaced Repetition Flashcards
          </p> 
        </div> 

        <button
          onClick={() => fileInputRef.current?.click()}
          className="flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-gradient-to-r from-pink-500 to-rose-500 hover:from-pink-400 text-white font-semibold text-xs shadow-md shadow-pink-500/20 transition-all active:scale-95"
        >
          <Plus className="w-3.5 h-3.5" /> 
          Import .apkg
        </button> 
        <input
          ref={fileInputRef}
          type="file"
          accept=".apkg,.colpkg"
          className="hidden"
          onChange={(e) => {
            if (e.target.files?.[0]) {
              handleFileUpload(e.target.files[0]) 
            } 
          }}
        /> 
      </div> 

      {totalDue > 0 && (
        <div className="mb-6 p-5 rounded-3xl glass-card border border-pink-500/30 bg-gradient-to-br from-pink-950/20 via-transparent to-purple-950/20 shadow-xl flex items-center justify-between">
          <div>
            <div className="text-xs font-bold uppercase tracking-wider text-pink-400 mb-1">
              Ready for Review
            </div> 
            <div className="text-2xl font-extrabold text-white">
              {totalDue} Cards Due
            </div> 
            <div className="text-xs text-slate-400 mt-0.5">
              Across all imported decks
            </div> 
          </div> 

          <button
            onClick={() => onSelectDeck(undefined)}
            className="flex items-center gap-2 px-5 py-3 rounded-2xl bg-gradient-to-r from-pink-500 to-rose-500 hover:from-pink-400 text-white font-semibold text-sm shadow-lg shadow-pink-500/30 transition-all active:scale-95"
          >
            <Play className="w-4 h-4 fill-current" /> 
            Study All
          </button> 
        </div> 
      )}

      <div
        onDragOver={onDragOver}
        onDrop={onDrop}
        onClick={() => fileInputRef.current?.click()}
        className="mb-8 p-6 rounded-3xl border-2 border-dashed border-white/15 hover:border-pink-500/40 bg-white/[0.02] hover:bg-white/[0.04] transition-all cursor-pointer text-center"
      >
        <div className="w-12 h-12 rounded-2xl bg-white/5 flex items-center justify-center mx-auto mb-3 text-slate-300">
          <UploadCloud className="w-6 h-6 text-pink-400" /> 
        </div> 
        <div className="text-sm font-semibold text-white mb-1">
          {isImporting ? 'Processing Anki Deck...' : 'Tap or Drag & Drop .apkg / .colpkg'} 
        </div> 
        <div className="text-xs text-slate-400 max-w-xs mx-auto">
          Import your Anki decks directly on iPhone Files app or Desktop. Cards are saved safely to offline IndexedDB.
        </div> 
      </div> 

      {importStatus && (
        <div className="mb-6 p-4 rounded-2xl bg-emerald-500/15 border border-emerald-500/30 text-emerald-300 text-xs flex items-center gap-2">
          <CheckCircle2 className="w-4 h-4 flex-shrink-0" /> 
          <span>{importStatus}</span> 
        </div> 
      )}

      {errorMessage && (
        <div className="mb-6 p-4 rounded-2xl bg-rose-500/15 border border-rose-500/30 text-rose-300 text-xs flex items-center gap-2">
          <AlertCircle className="w-4 h-4 flex-shrink-0" /> 
          <span>{errorMessage}</span> 
        </div> 
      )}

      <div className="space-y-3">
        {decks.map(deck => (
          <div
            key={deck.id}
            className="p-4 sm:p-5 rounded-2xl glass-card border border-white/10 hover:border-white/20 transition-all flex items-center justify-between"
          >
            <div className="flex items-center gap-3.5">
              <div className="w-11 h-11 rounded-xl bg-white/5 flex items-center justify-center text-pink-400 border border-white/5">
                <FolderOpen className="w-5 h-5" /> 
              </div> 
              <div>
                <div className="text-base font-semibold text-white">
                  {deck.name} 
                </div> 
                <div className="flex items-center gap-2 text-xs text-slate-400 mt-0.5">
                  <span>{deck.cardCount} cards total</span> 
                  <span>•</span> 
                  <span className="text-pink-300 font-medium">
                    {deck.dueCount} due
                  </span> 
                </div> 
              </div> 
            </div> 

            <button
              onClick={() => onSelectDeck(deck.id)}
              className="px-4 py-2 rounded-xl bg-white/10 hover:bg-white/20 text-white font-medium text-xs transition-all active:scale-95 flex items-center gap-1.5"
            >
              <BookOpen className="w-3.5 h-3.5" /> 
              Review
            </button> 
          </div> 
        ))}
      </div> 
    </div> 
  ) 
} 
