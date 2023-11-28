import React from 'react';

interface SearchBarProps {
  searchTerm: string;
  setSearchTerm: React.Dispatch<React.SetStateAction<string>>;
  placeholder: string;
}

const SearchBar: React.FC<SearchBarProps> = ({ searchTerm, setSearchTerm, placeholder }) => (
  <div className='input-wrapper'>
    <input
      type='text'
      placeholder={placeholder}
      value={searchTerm}
      onChange={(e) => setSearchTerm(e.target.value)}
      className='searchbar'
    />
    {searchTerm && (
      <button className='clear-input' onClick={() => setSearchTerm("")}>
        <i className="bi bi-x"></i>
      </button>
    )}
  </div>
);

export default SearchBar;
