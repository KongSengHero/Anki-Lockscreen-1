import React, { useState } from 'react' 
import { Upload, Check, Trash2, X, FolderOpen } from 'lucide-react' 
import type { AnkiDeck } from '../../../types/anki' 
import { parseAnkiDeck } from '../../../services/ankiParser' 
import { db } from '../../../services/db' 

interface DecksSheetProps {
  decks: AnkiDeck[] 
  selectedDeckId?: number 
  onSelectDeck: (deckId: number) => void 
  onDecksUpdated: () => void 
  onDismiss: () => void 
} 

export const DecksSheet: React.FC<DecksSheetProps> = ({
  decks, 
  selectedDeckId, 
  onSelectDeck, 
  onDecksUpdated, 
  onDismiss 
}) => {
  const [isImporting, setIsImporting] = useState(false) 
  const [importStatus, setImportStatus] = useState<string | null>(null) 

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] 
    if (!file) return 

    setIsImporting(true) 
    setImportStatus('Unpacking Anki deck database...') 

    try {
      const parsed = await parseAnkiDeck(file) 
      setImportStatus(`Imported ${parsed.cardCount} cards from "${parsed.deckName}"!`) 
      onSelectDeck(parsed.deckId) 
      onDecksUpdated() 

      setTimeout(() => {
        setIsImporting(false) 
        setImportStatus(null) 
      }, 1500) 
    } catch (err: any) {
      setImportStatus(`Import failed: ${err?.message || 'Invalid package'}`) 
      setIsImporting(false) 
    } 
  } 

  const handleDeleteDeck = async (e: React.MouseEvent, deckId: number) => {
    e.stopPropagation() 
    if (confirm('Delete this deck and its cards?')) {
      await db.cards.where('deckId').equals(deckId).delete() 
      await db.decks.delete(deckId) 
      onDecksUpdated() 
    } 
  } 

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/75 backdrop-blur-sm animate-fadeIn">
      <div className="w-full max-w-md max-h-[85vh] overflow-y-auto rounded-t-[24px] sm:rounded-[24px] bg-[#121418] border border-[#2C3240] p-6 shadow-2xl flex flex-col gap-4">
        <div className="flex items-center justify-between pb-2 border-b border-[#20242E]">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-[12px] bg-[#CFA055]/15 flex items-center justify-center text-[#CFA055]">
              <FolderOpen className="w-5 h-5" /> 
            </div> 
            <div>
              <h2 className="text-base font-bold text-[#E2E8F0]">
                Decks & Anki Sync
              </h2> 
              <p className="text-xs text-[#94A3B8]">
                Select active deck or import .apkg
              </p> 
            </div> 
          </div> 

          <button
            onClick={onDismiss}
            className="w-8 h-8 rounded-lg flex items-center justify-center text-[#94A3B8] hover:text-[#E2E8F0] hover:bg-white/5 active:scale-95"
          >
            <X className="w-5 h-5" /> 
          </button> 
        </div> 

        <label className="relative flex flex-col items-center justify-center p-4 border-2 border-dashed border-[#2C3240] hover:border-[#CFA055]/60 rounded-[16px] bg-[#1E222B]/50 cursor-pointer transition-all">
          <input
            type="file"
            accept=".apkg,.colpkg"
            onChange={handleFileUpload}
            disabled={isImporting}
            className="hidden"
          /> 
          <Upload className="w-6 h-6 text-[#CFA055] mb-2" /> 
          <span className="text-xs font-bold text-[#E2E8F0]">
            Import Anki Deck (.apkg / .colpkg)
          </span> 
          <span className="text-[11px] text-[#94A3B8] mt-0.5">
            Click to upload your exported cards
          </span> 
        </label> 

        {importStatus && (
          <div className="text-xs font-medium text-[#CFA055] bg-[#332717] border border-[#CFA055]/30 p-2.5 rounded-[10px] text-center animate-fadeIn">
            {importStatus} 
          </div> 
        )}

        <div className="flex flex-col gap-2 pt-2">
          <h3 className="text-xs font-bold uppercase text-[#94A3B8] tracking-wider">
            Available Decks ({decks.length})
          </h3> 

          {decks.map(deck => {
            const isSelected = selectedDeckId === deck.id 
            return (
              <div
                key={deck.id}
                onClick={() => onSelectDeck(deck.id)}
                className={`flex items-center justify-between p-3.5 rounded-[14px] cursor-pointer transition-all border ${
                  isSelected 
                    ? 'bg-[#332717] border-[#CFA055] shadow' 
                    : 'bg-[#1E222B] border-[#2C3240] hover:border-[#CFA055]/40' 
                }`}
              >
                <div className="flex items-center gap-3">
                  <div className={`w-5 h-5 rounded-full flex items-center justify-center border ${
                    isSelected 
                      ? 'bg-[#CFA055] border-[#CFA055] text-black' 
                      : 'border-[#64748B]' 
                  }`}>
                    {isSelected && <Check className="w-3 h-3 stroke-[3]" />}
                  </div> 
                  <div>
                    <h4 className="text-sm font-bold text-[#E2E8F0]">
                      {deck.name} 
                    </h4> 
                    <p className="text-[11px] text-[#94A3B8]">
                      {deck.cardCount} total cards · <span className="text-[#8AB4F8]">{deck.dueCount} due</span> 
                    </p> 
                  </div> 
                </div> 

                {decks.length > 1 && (
                  <button
                    onClick={(e) => handleDeleteDeck(e, deck.id)}
                    className="p-1.5 rounded-lg text-[#64748B] hover:text-[#EF4444] hover:bg-white/5 transition-colors"
                  >
                    <Trash2 className="w-4 h-4" /> 
                  </button> 
                )}
              </div> 
            ) 
          })}
        </div> 

        <button
          onClick={onDismiss}
          className="w-full py-2.5 rounded-[12px] bg-[#252A35] hover:bg-[#252A35]/80 text-[#E2E8F0] font-bold text-sm active:scale-95 transition-all mt-2"
        >
          Done
        </button> 
      </div> 
    </div> 
  ) 
} 
