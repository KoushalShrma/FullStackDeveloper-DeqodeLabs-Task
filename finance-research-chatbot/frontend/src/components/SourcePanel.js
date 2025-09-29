import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  List,
  ListItem,
  ListItemText,
  IconButton,
  Chip,
  Link,
  Collapse,
  Divider,
  CircularProgress
} from '@mui/material';
import {
  ExpandMore as ExpandMoreIcon,
  ExpandLess as ExpandLessIcon,
  Launch as LaunchIcon,
  Article as ArticleIcon
} from '@mui/icons-material';
import axios from 'axios';

const SourcePanel = ({ threadId }) => {
  const [sources, setSources] = useState([]);
  const [loading, setLoading] = useState(false);
  const [expanded, setExpanded] = useState(true);

  useEffect(() => {
    if (threadId) {
      fetchSources();
    }
  }, [threadId]);

  const fetchSources = async () => {
    if (!threadId) return;
    
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(
        `http://localhost:8080/api/sources/thread/${threadId}`,
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      setSources(response.data);
    } catch (error) {
      console.error('Error fetching sources:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleExpandClick = () => {
    setExpanded(!expanded);
  };

  const formatRelevanceScore = (score) => {
    if (!score) return 'N/A';
    return `${(score * 10).toFixed(1)}/10`;
  };

  const getDomainColor = (domain) => {
    const colors = {
      'hdfcbank.com': 'primary',
      'icicibank.com': 'secondary',
      'rbi.org.in': 'success',
      'moneycontrol.com': 'warning',
      'bloombergquint.com': 'info',
      'default': 'default'
    };
    return colors[domain] || colors.default;
  };

  if (!threadId) {
    return null;
  }

  return (
    <Paper sx={{ mb: 2, overflow: 'hidden' }}>
      {/* Header */}
      <Box
        sx={{
          p: 2,
          backgroundColor: '#f5f5f5',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          cursor: 'pointer'
        }}
        onClick={handleExpandClick}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <ArticleIcon color="primary" />
          <Typography variant="h6" color="primary">
            Research Sources ({sources.length})
          </Typography>
        </Box>
        
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          {loading && <CircularProgress size={20} />}
          <IconButton size="small">
            {expanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
          </IconButton>
        </Box>
      </Box>

      {/* Sources List */}
      <Collapse in={expanded}>
        <Box sx={{ maxHeight: 400, overflow: 'auto' }}>
          {sources.length === 0 ? (
            <Box sx={{ p: 3, textAlign: 'center' }}>
              <Typography color="text.secondary">
                No sources available for this conversation
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Sources will appear when conducting financial research
              </Typography>
            </Box>
          ) : (
            <List dense>
              {sources.map((source, index) => (
                <React.Fragment key={source.id}>
                  <ListItem
                    sx={{
                      alignItems: 'flex-start',
                      '&:hover': {
                        backgroundColor: '#f8f9fa'
                      }
                    }}
                  >
                    <ListItemText
                      primary={
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                          <Typography
                            variant="body2"
                            sx={{
                              fontWeight: 600,
                              color: 'primary.main',
                              minWidth: '20px',
                              textAlign: 'center'
                            }}
                          >
                            [{index + 1}]
                          </Typography>
                          <Typography variant="subtitle2" sx={{ flexGrow: 1 }}>
                            {source.title || 'Untitled Source'}
                          </Typography>
                          <Chip
                            label={formatRelevanceScore(source.relevanceScore)}
                            size="small"
                            color="primary"
                            variant="outlined"
                          />
                        </Box>
                      }
                      secondary={
                        <Box>
                          {/* Domain and URL */}
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                            <Chip
                              label={source.domain}
                              size="small"
                              color={getDomainColor(source.domain)}
                              variant="filled"
                              sx={{ fontSize: '0.7rem' }}
                            />
                            <Link
                              href={source.url}
                              target="_blank"
                              rel="noopener noreferrer"
                              sx={{
                                display: 'flex',
                                alignItems: 'center',
                                gap: 0.5,
                                fontSize: '0.8rem',
                                textDecoration: 'none',
                                '&:hover': { textDecoration: 'underline' }
                              }}
                            >
                              <LaunchIcon sx={{ fontSize: 14 }} />
                              Open Source
                            </Link>
                          </Box>
                          
                          {/* Snippet */}
                          {source.snippet && (
                            <Typography
                              variant="caption"
                              sx={{
                                display: 'block',
                                color: 'text.secondary',
                                backgroundColor: '#f8f9fa',
                                p: 1,
                                borderRadius: 1,
                                fontSize: '0.75rem',
                                lineHeight: 1.4,
                                border: '1px solid #e0e0e0'
                              }}
                            >
                              {source.snippet.length > 200
                                ? `${source.snippet.substring(0, 200)}...`
                                : source.snippet
                              }
                            </Typography>
                          )}
                        </Box>
                      }
                    />
                  </ListItem>
                  {index < sources.length - 1 && <Divider />}
                </React.Fragment>
              ))}
            </List>
          )}
        </Box>
      </Collapse>
    </Paper>
  );
};

export default SourcePanel;