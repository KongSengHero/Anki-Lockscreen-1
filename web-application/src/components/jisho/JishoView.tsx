import React, { useState, useEffect, useRef } from 'react' 
import { Search, X, RefreshCw, Trash2, History } from 'lucide-react' 
import type { JishoWord, SearchHistoryItem } from '../../types/jisho' 
import { searchJisho } from '../../services/jishoService' 
import { getSearchHistory, addSearchHistory, removeSearchHistory, clearSearchHistory } from '../../services/db' 
import { WordCard } from './WordCard' 
import { BlossomTactileButton } from '../blossom/BlossomTactileButton' 

const SUGGESTED_TERMS = [
  'JLPT N5', 'JLPT N4', 'JLPT N3', 'JLPT N2', 'JLPT N1', 
  '食べる', '飲む', '見る', '聞く', '話す', 
  '猫', '犬', '桜', 'ありがとう', '美しい', 
  '勉強', '友だち', '旅行', '家族', '時間', 
  '仕事', '日常', '音楽', '夢', '幸せ', 
  '自然', '心', '空', '雨', '海' 
] 

interface JishoViewProps {
  initialQuery?: string 
} 

export const JishoView: React.FC<JishoViewProps> = ({ initialQuery = '' }) => {
  const [query, setQuery] = useState(initialQuery) 
  const [results, setResults] = useState<JishoWord[]>([]) 
  const [history, setHistory] = useState<SearchHistoryItem[]>([]) 
  const [isLoading, setIsLoading] = useState(false) 
  const [errorMessage, setErrorMessage] = useState<string | null>(null) 
  const [searchToDelete, setSearchToDelete] = useState<string | null>(null) 

  const debounceTimerRef = useRef<any>(null) 
  const lastExecutedQueryRef = useRef('') 

  const half = Math.ceil(SUGGESTED_TERMS.length / 2) 
  const row1 = SUGGESTED_TERMS.slice(0, half) 
  const row2 = SUGGESTED_TERMS.slice(half) 

  const loadHistory = async () => {
    const hist = await getSearchHistory() 
    setHistory(hist) 
  } 

  const executeSearch = async (searchTerm: string) => {
    const trimmed = searchTerm.trim() 
    if (!trimmed) {
      setResults([]) 
      setIsLoading(false) 
      setErrorMessage(null) 
      lastExecutedQueryRef.current = '' 
      return 
    } 
    if (trimmed === lastExecutedQueryRef.current && results.length > 0) {
      return 
    } 

    lastExecutedQueryRef.current = trimmed 
    setIsLoading(true) 
    setErrorMessage(null) 

    try {
      const res = await searchJisho(trimmed) 
      setResults(res) 
      if (res.length > 0) {
        await addSearchHistory(trimmed) 
        await loadHistory() 
      } 
    } catch (err: any) {
      setErrorMessage(err?.message || 'Failed to connect to Jisho') 
    } finally {
      setIsLoading(false) 
    } 
  } 

  useEffect(() => {
    loadHistory() 
    if (initialQuery) {
      setQuery(initialQuery) 
      executeSearch(initialQuery) 
    } 
  }, [initialQuery]) 

  useEffect(() => {
    if (debounceTimerRef.current) {
      clearTimeout(debounceTimerRef.current) 
    } 
    if (!query.trim()) {
      setResults([]) 
      setIsLoading(false) 
      setErrorMessage(null) 
      return 
    } 
    debounceTimerRef.current = setTimeout(() => {
      executeSearch(query) 
    }, 500) 

    return () => {
      if (debounceTimerRef.current) clearTimeout(debounceTimerRef.current) 
    } 
  }, [query]) 

  const handleClearHistory = async () => {
    await clearSearchHistory() 
    setHistory([]) 
  } 

  const handleDeleteHistoryItem = async () => {
    if (!searchToDelete) return 
    await removeSearchHistory(searchToDelete) 
    setSearchToDelete(null) 
    await loadHistory() 
  } 

  return (
    <div className="w-full max-w-md mx-auto px-5 pt-20 pb-32 flex flex-col font-sans">
      <div className="py-2.5">
        <div
          className="relative flex items-center rounded-[24px] bg-[#252A35] border border-[#2C3240] focus-within:border-[#5FA77C] transition-all"
          style={{
            boxShadow: 'inset 0 1px 1px rgba(255, 255, 255, 0.08)' 
          }}
        >
          <div className="absolute left-4 pointer-events-none text-[#5FA77C]">
            <Search className="w-5 h-5" /> 
          </div> 
          <input
            type="text"
            value={query}
            onChange={e => setQuery(e.target.value)}
            onKeyDown={e => {
              if (e.key === 'Enter') executeSearch(query) 
            }}
            placeholder="Search Romaji, English, Kanji, Kana..."
            className="w-full pl-12 pr-11 py-3 rounded-[24px] bg-transparent text-sm font-semibold text-[#E8EAF0] placeholder-[#6E7482] outline-none"
          /> 
          {query && (
            <button
              onClick={() => {
                setQuery('') 
                setResults([]) 
                setErrorMessage(null) 
              }}
              className="absolute right-3.5 p-1 rounded-full text-[#9AA1AD] hover:text-[#E8EAF0]"
            >
              <X className="w-4 h-4" /> 
            </button> 
          )}
        </div> 
      </div> 

      {query.trim() === '' ? (
        <div className="flex flex-col gap-6 pt-2">
          {history.length > 0 && (
            <div>
              <div className="flex items-center justify-between mb-3">
                <div
                  className="px-2.5 py-1 rounded-[8px] bg-[#252A35] border border-[#2C3240]"
                  style={{
                    boxShadow: 'inset 0 1px 1px rgba(255, 255, 255, 0.08)' 
                  }}
                >
                  <span className="text-[11px] font-bold text-[#9AA1AD] tracking-[0.8px]">
                    RECENT SEARCHES
                  </span> 
                </div> 

                <button
                  onClick={handleClearHistory}
                  className="text-xs font-bold text-[#5FA77C] hover:underline"
                >
                  Clear
                </button> 
              </div> 

              <div className="flex flex-wrap gap-2">
                {history.map((h, idx) => (
                  <div
                    key={idx}
                    onClick={() => {
                      setQuery(h.query) 
                      executeSearch(h.query) 
                    }}
                    onContextMenu={(e) => {
                      e.preventDefault() 
                      setSearchToDelete(h.query) 
                    }}
                    className="flex items-center px-3 py-1.5 rounded-[16px] bg-[#252A35] border border-[#2C3240] hover:border-[#5FA77C]/50 text-xs font-medium text-[#E8EAF0] cursor-pointer transition-all active:scale-95 group"
                    style={{
                      boxShadow: 'inset 0 1px 1px rgba(255, 255, 255, 0.08)' 
                    }}
                  >
                    <History className="w-[13px] h-[13px] text-[#6E7482] mr-1.5 shrink-0" /> 
                    <span>{h.query}</span> 
                    <button
                      onClick={(e) => {
                        e.stopPropagation() 
                        setSearchToDelete(h.query) 
                      }}
                      className="ml-2 text-[#6E7482] hover:text-[#E87A90] opacity-50 group-hover:opacity-100 transition-opacity"
                    >
                      <X className="w-3 h-3" /> 
                    </button> 
                  </div> 
                ))}
              </div> 
            </div> 
          )}

          <div>
            <div
              className="inline-block px-2.5 py-1 rounded-[8px] bg-[#252A35] border border-[#2C3240] mb-3"
              style={{
                boxShadow: 'inset 0 1px 1px rgba(255, 255, 255, 0.08)' 
              }}
            >
              <span className="text-[11px] font-bold text-[#9AA1AD] tracking-[0.8px]">
                SUGGESTED EXPLORATIONS
              </span> 
            </div> 

            <div className="flex flex-col gap-2 overflow-x-auto no-scrollbar py-1 -mx-5 px-5">
              <div className="flex gap-2 min-w-max">
                {row1.map((term, idx) => (
                  <button
                    key={idx}
                    onClick={() => {
                      setQuery(term) 
                      executeSearch(term) 
                    }}
                    className="px-3 py-1.5 rounded-[16px] bg-[#1B3024]/50 border border-[#5FA77C]/35 text-[#5FA77C] text-[13px] font-semibold hover:bg-[#1B3024]/80 active:scale-95 transition-all"
                  >
                    {term} 
                  </button> 
                ))}
              </div> 

              <div className="flex gap-2 min-w-max">
                {row2.map((term, idx) => (
                  <button
                    key={idx}
                    onClick={() => {
                      setQuery(term) 
                      executeSearch(term) 
                    }}
                    className="px-3 py-1.5 rounded-[16px] bg-[#1B3024]/50 border border-[#5FA77C]/35 text-[#5FA77C] text-[13px] font-semibold hover:bg-[#1B3024]/80 active:scale-95 transition-all"
                  >
                    {term} 
                  </button> 
                ))}
              </div> 
            </div> 
          </div> 

          <div
            className="rounded-[16px] bg-[#252A35] border border-[#2C3240] p-[18px]"
            style={{
              boxShadow: 'inset 0 1px 1px rgba(255, 255, 255, 0.08)' 
            }}
          >
            <div className="flex items-center gap-2 mb-1.5">
              <Search className="w-[18px] h-[18px] text-[#5FA77C]" /> 
              <h3 className="text-[15px] font-bold text-[#E8EAF0]">
                Universal Search
              </h3> 
            </div> 
            <p className="text-[13px] text-[#9AA1AD] leading-5">
              Type English words (&apos;eat&apos;), Romaji (&apos;taberu&apos;), Kanji (&apos;食べる&apos;), or Kana (&apos;たべる&apos;). You can also search by level, e.g. &apos;#jlpt-n5&apos;.
            </p> 
          </div> 
        </div> 
      ) : isLoading ? (
        <div className="flex flex-col items-center justify-center py-28">
          <div className="w-9 h-9 border-3 border-[#5FA77C] border-t-transparent rounded-full animate-spin" /> 
          <span className="mt-3.5 text-sm font-semibold text-[#9AA1AD]">
            Searching Jisho...
          </span> 
        </div> 
      ) : errorMessage ? (
        <div className="flex flex-col items-center justify-center py-20 px-6 text-center">
          <p className="text-sm text-[#E87A90] mb-4 font-semibold">
            {errorMessage} 
          </p> 
          <BlossomTactileButton
            onClick={() => executeSearch(query)}
            variant="matcha"
            className="w-auto px-6"
          >
            <RefreshCw className="w-4 h-4 mr-2" /> 
            Retry
          </BlossomTactileButton> 
        </div> 
      ) : results.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-20 px-6 text-center">
          <h3 className="text-base font-bold text-[#E8EAF0] mb-2">
            No results found for &quot;{query}&quot;
          </h3> 
          <p className="text-[13px] text-[#9AA1AD] max-w-xs leading-relaxed">
            Try checking the spelling or searching using Romaji or plain English.
          </p> 
        </div> 
      ) : (
        <div className="flex flex-col gap-3.5 pt-1">
          <div className="text-[11px] font-bold text-[#6E7482] tracking-[0.8px] py-1 uppercase">
            {results.length} RESULTS FOR &quot;{query}&quot;
          </div> 

          {results.map((word) => (
            <WordCard key={word.slug} word={word} /> 
          ))}
        </div> 
      )}

      {searchToDelete && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-fadeIn">
          <div className="w-full max-w-sm rounded-[20px] bg-[#1E222B] border border-[#2C3240] p-[22px] shadow-2xl flex flex-col gap-3.5">
            <div className="flex items-center gap-2.5">
              <Trash2 className="w-6 h-6 text-[#E87A90]" /> 
              <h3 className="text-lg font-bold text-[#E8EAF0]">
                Delete Search History?
              </h3> 
            </div> 

            <p className="text-[13px] text-[#9AA1AD] leading-5">
              Are you sure you want to delete &quot;{searchToDelete}&quot; from your recent searches? This action cannot be undone.
            </p> 

            <div className="flex items-center gap-2.5 pt-2">
              <BlossomTactileButton
                onClick={() => setSearchToDelete(null)}
                variant="secondary"
                className="flex-1"
              >
                Cancel
              </BlossomTactileButton> 

              <BlossomTactileButton
                onClick={handleDeleteHistoryItem}
                variant="danger"
                className="flex-1"
              >
                Delete
              </BlossomTactileButton> 
            </div> 
          </div> 
        </div> 
      )}
    </div> 
  ) 
} 
