import React from 'react';
import styled from 'styled-components';
import { Link } from 'react-router-dom';

const BlogButton = ({ to }) => {
    return (
        <StyledWrapper>
            <Link to={to}>
                <button>Blog</button>
            </Link>
        </StyledWrapper>
    );
}

const StyledWrapper = styled.div`
  button {
    margin: 12px;
    height: 50px;
    width: 120px;
    border-radius: 10px;
    background: #333;
    justify-content: center;
    align-items: center;
    box-shadow: -5px -5px 15px #444, 5px 5px 15px #222, inset 5px 5px 10px #444,
      inset -5px -5px 10px #222;
    font-family: "Damion", cursive;
    cursor: pointer;
    border: none;
    font-size: 16px;
    color: rgb(161, 161, 161);
    transition: 500ms;
  }

  button:hover {
    box-shadow: -5px -5px 15px #444, 5px 5px 15px #222, inset 5px 5px 10px #222,
      inset -5px -5px 10px #444;
    color: #d6d6d6;
    transition: 500ms;
  }`;

export default BlogButton;
