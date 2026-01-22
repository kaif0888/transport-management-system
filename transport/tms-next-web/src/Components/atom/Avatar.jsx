'use client'

import React from 'react';

const Avatar = ({ 
  name, 
  src, 
  size = 'md', 
  className = '' 
}) => {
  const sizeClasses = {
    sm: 'w-8 h-8 text-sm',
    md: 'w-10 h-10 text-base',
    lg: 'w-12 h-12 text-lg',
    xl: 'w-16 h-16 text-xl'
  };

  const getInitials = (name) => {
    return name
      .split(' ')
      .map(word => word[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  const baseClasses = `
    inline-flex items-center justify-center rounded-full 
    bg-[#750014] text-white font-medium transition-all
    hover:bg-[#750014]/90 cursor-pointer
    ${sizeClasses[size]} ${className}
  `;

  if (src) {
    return (
      <img
        src={src}
        alt={name}
        className={`${baseClasses} object-cover`}
      />
    );
  }

  return (
    <div className={baseClasses}>
      {getInitials(name)}
    </div>
  );
};

export default Avatar;