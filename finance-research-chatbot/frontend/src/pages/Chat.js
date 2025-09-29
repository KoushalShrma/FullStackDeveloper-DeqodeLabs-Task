import React, { useState, useEffect } from 'react';
import ReactMarkdown from 'react-markdown';
import {
  Box,
  Container,
  AppBar,
  Toolbar,
  Typography,
  Button,
  Grid,
  Paper,
  List,
  ListItem,
  ListItemText,
  IconButton,
  TextField,
  InputAdornment,
  CircularProgress,
  LinearProgress,
  Chip,
  Fade,
} from '@mui/material';
import {
  Send as SendIcon,
  Add as AddIcon,
  Logout as LogoutIcon,
  Psychology as PsychologyIcon,
  Download as DownloadIcon,
  FileDownload as FileDownloadIcon,
  HourglassEmpty as HourglassEmptyIcon,
  AutoAwesome as AutoAwesomeIcon,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import SourcePanel from '../components/SourcePanel';
import axios from 'axios';

const Chat = () => {
  const { user, logout } = useAuth();
  const [threads, setThreads] = useState([]);
  const [currentThread, setCurrentThread] = useState(null);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [streamingMessage, setStreamingMessage] = useState('');
  const [isStreaming, setIsStreaming] = useState(false);
  const [isWaitingForResponse, setIsWaitingForResponse] = useState(false);
  const [processingStage, setProcessingStage] = useState('');

  useEffect(() => {
    loadThreads();
  }, []);

  useEffect(() => {
    if (currentThread) {
      loadMessages(currentThread.id);
      
      // Set up polling for new messages every 3 seconds when in a thread
      const interval = setInterval(() => {
        loadMessages(currentThread.id);
      }, 3000);
      
      return () => clearInterval(interval);
    }
  }, [currentThread]);

  const loadThreads = async () => {
    try {
      const response = await axios.get('/api/threads');
      setThreads(response.data);
      if (response.data.length > 0 && !currentThread) {
        setCurrentThread(response.data[0]);
      }
    } catch (error) {
      console.error('Failed to load threads:', error);
    }
  };

  const loadMessages = async (threadId) => {
    try {
      const response = await axios.get(`/api/threads/${threadId}/messages`);
      setMessages(response.data);
    } catch (error) {
      console.error('Failed to load messages:', error);
    }
  };

  const createNewThread = async () => {
    try {
      const response = await axios.post('/api/threads', {
        title: 'New Research Thread',
      });
      const newThread = response.data;
      setThreads([newThread, ...threads]);
      setCurrentThread(newThread);
      setMessages([]);
    } catch (error) {
      console.error('Failed to create thread:', error);
    }
  };

  const sendMessage = async () => {
    if (!newMessage.trim() || !currentThread) return;

    console.log('Starting sendMessage - setting loading states');
    setLoading(true);
    setIsStreaming(true);
    setIsWaitingForResponse(true);
    setStreamingMessage('');
    setProcessingStage('Sending your message...');
    console.log('✅ Loading states set - should show animations now');
    
    try {
      // First, send the message using the streaming endpoint
      setProcessingStage('Processing your request...');
      console.log('📤 Sending request to backend');
      const response = await axios.post(`/api/stream/message/${currentThread.id}`, {
        content: newMessage,
      });
      console.log('📥 Response received:', response.data);
      
      // Add user message to the UI immediately
      const userMsg = {
        id: Date.now(),
        content: newMessage,
        sender: 'USER',
        timestamp: new Date().toISOString()
      };
      setMessages(prev => [...prev, userMsg]);
      setNewMessage('');
      setProcessingStage('Connecting to AI assistant...');
      console.log('💬 User message added to UI');
      
      // Get connection ID from response
      const connectionId = response.data.connectionId;
      console.log('🔗 Connection ID:', connectionId);
      
      // Start listening to the SSE stream
      const eventSource = new EventSource(`/api/stream/connect/${connectionId}`);
      
      eventSource.onopen = () => {
        console.log('🌐 SSE connection opened');
        setProcessingStage('Connected! Waiting for response...');
      };
      
      eventSource.addEventListener('connected', (event) => {
        console.log('🔌 Connected to stream:', event.data);
        setProcessingStage('AI is thinking...');
      });
      
      eventSource.addEventListener('started', (event) => {
        console.log('🤖 AI response started:', event.data);
        setProcessingStage('Generating response...');
        setIsWaitingForResponse(false);
      });
      
      eventSource.addEventListener('chunk', (event) => {
        const data = JSON.parse(event.data);
        console.log('📝 Received chunk:', data.chunk);
        setStreamingMessage(prev => prev + data.chunk);
        setProcessingStage('');
      });
      
      eventSource.addEventListener('completed', (event) => {
        console.log('✅ Stream completed:', event.data);
        setIsStreaming(false);
        setIsWaitingForResponse(false);
        setProcessingStage('');
        
        // Reload messages to get the final saved message
        setTimeout(() => {
          loadMessages(currentThread.id);
          setStreamingMessage('');
        }, 500);
        
        eventSource.close();
      });
      
      eventSource.addEventListener('error', (event) => {
        console.error('❌ Stream error:', event.data);
        setIsStreaming(false);
        setIsWaitingForResponse(false);
        setStreamingMessage('');
        setProcessingStage('');
        eventSource.close();
      });
      
      eventSource.onerror = (error) => {
        console.error('❌ SSE connection error:', error);
        setIsStreaming(false);
        setIsWaitingForResponse(false);
        setStreamingMessage('');
        setProcessingStage('');
        eventSource.close();
      };
      
    } catch (error) {
      console.error('❌ Failed to send message:', error);
      setIsStreaming(false);
      setIsWaitingForResponse(false);
      setStreamingMessage('');
      setProcessingStage('');
    } finally {
      setLoading(false);
      console.log('🏁 Finally block - setting loading to false');
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  const downloadReport = async (format) => {
    if (!currentThread) return;
    
    try {
      const response = await axios.get(`/api/reports/thread/${currentThread.id}/${format}`, {
        responseType: 'blob'
      });
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `finance-research-report-${currentThread.id}.${format}`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error(`Failed to download ${format} report:`, error);
    }
  };

  return (
    <Box sx={{ flexGrow: 1, height: '100vh', display: 'flex', flexDirection: 'column' }}>
      <AppBar position="static">
        <Toolbar>
          <PsychologyIcon sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Finance Research Chatbot
          </Typography>
          <Typography variant="body2" sx={{ mr: 2 }}>
            Welcome, {user?.fullName || user?.email}
          </Typography>
          {currentThread && (
            <>
              <Button
                color="inherit"
                startIcon={<DownloadIcon />}
                onClick={() => downloadReport('markdown')}
                sx={{ mr: 1 }}
              >
                MD
              </Button>
              <Button
                color="inherit"
                startIcon={<FileDownloadIcon />}
                onClick={() => downloadReport('html')}
                sx={{ mr: 2 }}
              >
                HTML
              </Button>
            </>
          )}
          <IconButton color="inherit" onClick={logout}>
            <LogoutIcon />
          </IconButton>
        </Toolbar>
      </AppBar>

      <Box sx={{ flexGrow: 1, display: 'flex' }}>
        {/* Sidebar - Thread List */}
        <Paper
          sx={{
            width: 300,
            borderRadius: 0,
            borderRight: 1,
            borderColor: 'divider',
            height: 'calc(100vh - 64px)',
            overflow: 'auto',
          }}
        >
          <Box sx={{ p: 2 }}>
            <Button
              fullWidth
              variant="contained"
              startIcon={<AddIcon />}
              onClick={createNewThread}
            >
              New Research Thread
            </Button>
          </Box>
          
          <List>
            {threads.map((thread) => (
              <ListItem
                key={thread.id}
                button
                selected={currentThread?.id === thread.id}
                onClick={() => setCurrentThread(thread)}
              >
                <ListItemText
                  primary={thread.title || 'Untitled Thread'}
                  secondary={`${thread.messageCount} messages`}
                />
              </ListItem>
            ))}
          </List>
        </Paper>

        {/* Main Chat Area */}
        <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
          {currentThread ? (
            <>
              {/* Messages */}
              <Box
                sx={{
                  flexGrow: 1,
                  overflow: 'auto',
                  p: 2,
                  backgroundColor: '#f5f5f5',
                }}
              >
                {/* Processing Status Bar */}
                {(isStreaming || isWaitingForResponse) && (
                  <>
                    {console.log('🎨 Rendering processing status bar - isStreaming:', isStreaming, 'isWaitingForResponse:', isWaitingForResponse, 'processingStage:', processingStage)}
                    <Fade in={true}>
                      <Paper
                        sx={{
                          p: 2,
                          mb: 2,
                          backgroundColor: '#e3f2fd',
                          borderLeft: '4px solid #1976d2',
                          display: 'flex',
                          alignItems: 'center',
                          gap: 2,
                        }}
                      >
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          {isWaitingForResponse ? (
                            <HourglassEmptyIcon
                              sx={{
                                animation: 'spin 2s linear infinite',
                                '@keyframes spin': {
                                  '0%': { transform: 'rotate(0deg)' },
                                  '100%': { transform: 'rotate(360deg)' }
                                }
                              }}
                            />
                          ) : (
                            <AutoAwesomeIcon
                              sx={{
                                animation: 'pulse 1.5s ease-in-out infinite',
                                '@keyframes pulse': {
                                  '0%': { opacity: 0.5, transform: 'scale(1)' },
                                  '50%': { opacity: 1, transform: 'scale(1.1)' },
                                  '100%': { opacity: 0.5, transform: 'scale(1)' }
                                }
                              }}
                            />
                          )}
                          <Typography variant="body2" color="primary" sx={{ fontWeight: 'medium' }}>
                            {processingStage || (isWaitingForResponse ? 'Preparing your response...' : 'AI is responding...')}
                          </Typography>
                        </Box>
                        <Box sx={{ flexGrow: 1 }}>
                          <LinearProgress 
                            sx={{ 
                              borderRadius: 2,
                              height: 6,
                              backgroundColor: '#bbdefb',
                              '& .MuiLinearProgress-bar': {
                                backgroundColor: '#1976d2'
                              }
                            }} 
                          />
                        </Box>
                      </Paper>
                    </Fade>
                  </>
                )}

                {messages.map((message) => (
                  <Paper
                    key={message.id}
                    sx={{
                      p: 2,
                      mb: 2,
                      backgroundColor: message.role === 'user' ? '#e3f2fd' : '#fff',
                      ml: message.role === 'user' ? 4 : 0,
                      mr: message.role === 'user' ? 0 : 4,
                    }}
                  >
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      {message.role === 'user' ? 'You' : 'Assistant'}
                    </Typography>
                    {message.role === 'user' ? (
                      <Typography variant="body1">
                        {message.content}
                      </Typography>
                    ) : (
                      <Box sx={{ 
                        '& h2': { fontSize: '1.2rem', fontWeight: 'bold', mb: 1, mt: 1 },
                        '& h3': { fontSize: '1.1rem', fontWeight: 'bold', mb: 0.5, mt: 1 },
                        '& p': { mb: 1 },
                        '& ul': { pl: 2 },
                        '& li': { mb: 0.5 },
                        '& strong': { fontWeight: 'bold' },
                        '& code': { backgroundColor: '#f5f5f5', padding: '2px 4px', borderRadius: '3px' }
                      }}>
                        <ReactMarkdown>{message.content}</ReactMarkdown>
                      </Box>
                    )}
                    {message.reasoningTrace && (
                      <Box sx={{ mt: 1, p: 1, backgroundColor: '#f0f0f0', borderRadius: 1 }}>
                        <Typography variant="caption" color="text.secondary">
                          Reasoning:
                        </Typography>
                        <Typography variant="body2" sx={{ mt: 0.5 }}>
                          {message.reasoningTrace}
                        </Typography>
                      </Box>
                    )}
                  </Paper>
                ))}
                
                {/* Enhanced Streaming Message */}
                {isStreaming && streamingMessage && (
                  <Fade in={true}>
                    <Paper
                      sx={{
                        p: 2,
                        mb: 2,
                        backgroundColor: '#fff',
                        mr: 4,
                        border: '2px solid #4caf50',
                        borderRadius: 2,
                        position: 'relative',
                        '&::before': {
                          content: '""',
                          position: 'absolute',
                          top: 0,
                          left: 0,
                          right: 0,
                          height: '4px',
                          background: 'linear-gradient(90deg, transparent, #4caf50, transparent)',
                          animation: 'shimmer 2s infinite',
                          '@keyframes shimmer': {
                            '0%': { transform: 'translateX(-100%)' },
                            '100%': { transform: 'translateX(100%)' }
                          }
                        }
                      }}
                    >
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 1, gap: 1 }}>
                        <AutoAwesomeIcon sx={{ color: '#4caf50', fontSize: '1.2rem' }} />
                        <Typography variant="body2" color="text.secondary">
                          Assistant (responding...)
                        </Typography>
                        <Chip
                          label="LIVE"
                          size="small"
                          sx={{
                            backgroundColor: '#4caf50',
                            color: 'white',
                            fontSize: '0.7rem',
                            height: '20px',
                            animation: 'blink 1s infinite',
                            '@keyframes blink': {
                              '0%': { opacity: 1 },
                              '50%': { opacity: 0.5 },
                              '100%': { opacity: 1 }
                            }
                          }}
                        />
                      </Box>
                      <Box sx={{ 
                        '& h2': { fontSize: '1.2rem', fontWeight: 'bold', mb: 1, mt: 1 },
                        '& h3': { fontSize: '1.1rem', fontWeight: 'bold', mb: 0.5, mt: 1 },
                        '& p': { mb: 1 },
                        '& ul': { pl: 2 },
                        '& li': { mb: 0.5 },
                        '& strong': { fontWeight: 'bold' },
                        '& code': { backgroundColor: '#f5f5f5', padding: '2px 4px', borderRadius: '3px' }
                      }}>
                        <ReactMarkdown>{streamingMessage}</ReactMarkdown>
                      </Box>
                    </Paper>
                  </Fade>
                )}
                
                {/* Enhanced Waiting Indicator */}
                {isStreaming && !streamingMessage && (
                  <Fade in={true}>
                    <Paper
                      sx={{
                        p: 3,
                        mb: 2,
                        backgroundColor: '#f8f9fa',
                        mr: 4,
                        borderRadius: 2,
                        border: '1px solid #e0e0e0',
                        textAlign: 'center',
                      }}
                    >
                      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 2, mb: 1 }}>
                        <CircularProgress size={24} thickness={4} />
                        <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 'medium' }}>
                          {processingStage || 'AI is analyzing your request...'}
                        </Typography>
                      </Box>
                      <Typography variant="caption" color="text.secondary" sx={{ fontStyle: 'italic' }}>
                        This may take a few moments while we research your query
                      </Typography>
                    </Paper>
                  </Fade>
                )}
              </Box>

              {/* Message Input */}
              <Paper sx={{ p: 2, borderRadius: 0, backgroundColor: loading ? '#f5f5f5' : 'white' }}>
                {/* Input Status Bar */}
                {loading && (
                  <Box sx={{ mb: 2 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                      <CircularProgress size={16} />
                      <Typography variant="caption" color="text.secondary">
                        {processingStage || 'Processing your message...'}
                      </Typography>
                    </Box>
                    <LinearProgress 
                      sx={{ 
                        borderRadius: 1,
                        height: 3,
                        backgroundColor: '#e0e0e0',
                      }} 
                    />
                  </Box>
                )}
                
                <TextField
                  fullWidth
                  multiline
                  maxRows={4}
                  placeholder={loading ? "Please wait while we process your previous message..." : "Ask about financial research, market analysis, or company evaluation..."}
                  value={newMessage}
                  onChange={(e) => setNewMessage(e.target.value)}
                  onKeyPress={handleKeyPress}
                  disabled={loading}
                  sx={{
                    '& .MuiInputBase-root': {
                      backgroundColor: loading ? '#fafafa' : 'white',
                    },
                    '& .MuiInputBase-input': {
                      opacity: loading ? 0.6 : 1,
                    }
                  }}
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          onClick={sendMessage}
                          disabled={loading || !newMessage.trim()}
                          color="primary"
                          sx={{
                            backgroundColor: loading ? 'transparent' : (!newMessage.trim() ? 'transparent' : '#1976d2'),
                            color: loading ? 'text.disabled' : (!newMessage.trim() ? 'text.disabled' : 'white'),
                            '&:hover': {
                              backgroundColor: loading ? 'transparent' : (!newMessage.trim() ? 'transparent' : '#1565c0'),
                            },
                            '&.Mui-disabled': {
                              backgroundColor: 'transparent',
                            }
                          }}
                        >
                          {loading ? <CircularProgress size={20} /> : <SendIcon />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />
                
                {/* Status text below input */}
                {loading && (
                  <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block', textAlign: 'center' }}>
                    🤖 Your message is being processed. Response will appear above shortly...
                  </Typography>
                )}
              </Paper>
            </>
          ) : (
            <Box
              sx={{
                flexGrow: 1,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <Typography variant="h6" color="text.secondary">
                Select a thread or create a new one to start researching
              </Typography>
            </Box>
          )}
        </Box>

        {/* Sources Sidebar */}
        <Box sx={{ width: 350, backgroundColor: '#fafafa', overflow: 'auto', borderLeft: 1, borderColor: 'divider' }}>
          <SourcePanel threadId={currentThread?.id} />
        </Box>
      </Box>
    </Box>
  );
};

export default Chat;