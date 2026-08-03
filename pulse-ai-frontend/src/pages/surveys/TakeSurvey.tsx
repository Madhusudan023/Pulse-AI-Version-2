import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axios from 'axios';
import { useAuthStore } from '../../store/useAuthStore';
import { getSurveyDetails, submitSurvey, type SubmitSurveyRequest } from '../../services/survey.service';
import { Sparkles, ArrowRight, ArrowLeft, CheckCircle2, AlertCircle } from 'lucide-react';
import { GlassCard } from '../../components/dashboard/GlassCard';

interface Question {
  id: number;
  // Support all possible field names the backend might return
  questionText?: string;
  text?: string;
  question?: string;
  content?: string;
  questionType?: string;
  type?: string;
  category?: string;
  options?: string;
  choices?: string;
  displayOrder?: number;
  orderIndex?: number;
  [key: string]: any; // allow any other fields
}

const getQuestionText = (q?: Question): string => {
  if (!q) return '';
  return q.questionText || q.text || q.question || q.content || 
    // fallback: find any string field that looks like a question
    Object.values(q).find(v => typeof v === 'string' && v.length > 10 && v !== q.questionType && v !== q.type) as string || '';
};

const getQuestionType = (q?: Question): 'RATING' | 'OPEN_ENDED' | 'MULTIPLE_CHOICE' => {
  const t = (q?.questionType || q?.type || q?.category || '').toUpperCase();
  if (t.includes('RATING') || t.includes('LIKERT')) return 'RATING';
  if (t.includes('MULTIPLE') || t.includes('CHOICE') || t.includes('MCQ')) return 'MULTIPLE_CHOICE';
  return 'OPEN_ENDED'; // default
};

const getOptions = (q?: Question): string[] => {
  if (!q) return [];
  const opts = q.options || q.choices || '';
  if (!opts) return [];
  return opts.split(',').map((o: string) => o.trim()).filter(Boolean);
};

// Helper: parse custom rating ranges (like 1-10 or 1-20) from question text
const getRatingRange = (text: string): { min: number; max: number } => {
  const match = text.match(/\(?(\d+)\s*(?:-|to)\s*(\d+)\)?/i);
  if (match) {
    const min = parseInt(match[1]);
    const max = parseInt(match[2]);
    if (min < max) return { min, max };
  }
  return { min: 1, max: 10 }; // default
};

export const TakeSurvey = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  
  const [currentStep, setCurrentStep] = useState(0);
  const [answers, setAnswers] = useState<Record<number, any>>({});
  const [startTime] = useState<number>(Date.now());
  const [isSuccess, setIsSuccess] = useState(false);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['surveyDetails', id],
    queryFn: () => getSurveyDetails(id!),
    enabled: !!id,
  });

  // Extract question IDs from raw survey questions to bulk fetch their full details
  const rawPayload = data;
  const survey = rawPayload?.survey || rawPayload;
  const rawQuestions: Question[] = 
    rawPayload?.questions ||
    rawPayload?.survey?.questions ||
    rawPayload?.surveyQuestions ||
    rawPayload?.data?.questions ||
    (Array.isArray(rawPayload) ? rawPayload : []);

  const questionIds = rawQuestions.map(q => q.questionId || q.id).filter(Boolean);

  const { data: fullQuestions, isLoading: isLoadingQuestions } = useQuery({
    queryKey: ['questions', 'bulk', questionIds.join(',')],
    queryFn: async () => {
      if (questionIds.length === 0) return [];
      const token = useAuthStore.getState().token;
      const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';
      const res = await axios.post(
        `${API_BASE}/question-bank-service/api/v1/internal/questions/bulk-fetch`,
        questionIds,
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );
      return res.data;
    },
    enabled: questionIds.length > 0,
  });

  const submitMutation = useMutation({
    mutationFn: (payload: SubmitSurveyRequest) => submitSurvey(id!, payload),
    onSuccess: () => {
      setIsSuccess(true);
      queryClient.invalidateQueries({ queryKey: ['surveys', 'PENDING'] });
      queryClient.invalidateQueries({ queryKey: ['surveys', 'COMPLETED'] });
      setTimeout(() => navigate('/surveys'), 3000);
    },
    onError: (err: any) => {
      console.error('Survey submission error:', err?.response?.data || err);
      alert('Failed to submit. Please try again.');
    }
  });

  // Log the raw API response so we can see exact field names in DevTools
  useEffect(() => {
    if (data) {
      console.log('[TakeSurvey] Raw API data:', JSON.stringify(data, null, 2));
    }
  }, [data]);



  // Map the SurveyQuestions (which only have questionId) to the full details from the question bank
  const questions = rawQuestions.map(q => {
    const fullQ = fullQuestions?.find((aq: any) => aq.id === q.questionId || aq.id === q.id);
    return {
      ...q,
      ...fullQ,
      id: q.questionId || q.id // Ensure we use the actual question ID (e.g. 24) instead of the survey-question mapping ID!
    };
  });

  const currentQuestion = questions[currentStep];

  const handleNext = () => {
    if (currentStep < questions.length - 1) {
      setCurrentStep(s => s + 1);
    } else {
      handleSubmit();
    }
  };

  const handlePrev = () => {
    if (currentStep > 0) setCurrentStep(s => s - 1);
  };

  const handleAnswer = (val: any) => {
    if (!currentQuestion) return;
    setAnswers(prev => ({ ...prev, [currentQuestion.id]: val }));
  };

  const handleRatingChange = (qId: number, ratingVal: number) => {
    setAnswers(prev => {
      const current = prev[qId] && typeof prev[qId] === 'object' ? prev[qId] : {};
      return {
        ...prev,
        [qId]: { ...current, rating: ratingVal }
      };
    });
  };

  const handleCommentChange = (qId: number, commentVal: string) => {
    setAnswers(prev => {
      const current = prev[qId] && typeof prev[qId] === 'object' ? prev[qId] : {};
      return {
        ...prev,
        [qId]: { ...current, comment: commentVal }
      };
    });
  };

  const handleSubmit = () => {
    const durationSecs = Math.floor((Date.now() - startTime) / 1000);
    
    const formattedAnswers = questions.map(q => {
      const ans = answers[q.id];
      const qType = getQuestionType(q);
      
      const answerPayload: any = {
        questionId: q.id
      };
      
      if (qType === 'RATING') {
        const rating = ans && typeof ans === 'object' ? ans.rating : undefined;
        const comment = ans && typeof ans === 'object' ? ans.comment : '';
        answerPayload.ratingAnswer = rating !== undefined && rating !== null ? Number(rating) : null;
        answerPayload.textAnswer = comment ? String(comment) : null;
        answerPayload.optionAnswer = null;
      } else if (qType === 'OPEN_ENDED') {
        answerPayload.ratingAnswer = null;
        answerPayload.textAnswer = ans !== undefined && ans !== null ? String(ans) : null;
        answerPayload.optionAnswer = null;
      } else if (qType === 'MULTIPLE_CHOICE') {
        answerPayload.ratingAnswer = null;
        answerPayload.textAnswer = null;
        answerPayload.optionAnswer = ans !== undefined && ans !== null ? String(ans) : null;
      } else {
        answerPayload.ratingAnswer = null;
        answerPayload.textAnswer = null;
        answerPayload.optionAnswer = null;
      }
      
      return answerPayload;
    });

    submitMutation.mutate({
      responseDuration: String(durationSecs),
      answers: formattedAnswers
    });
  };

  const isLoadingAll = isLoading || isLoadingQuestions;

  if (isLoadingAll) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-4">
        <div className="w-12 h-12 border-4 border-primary/30 border-t-primary rounded-full animate-spin"></div>
        <p className="text-gray-500">Loading your survey...</p>
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-6 text-center px-4">
        <div className="w-20 h-20 rounded-full bg-amber-500/10 flex items-center justify-center">
          <AlertCircle className="w-10 h-10 text-amber-500" />
        </div>
        <div>
          <h2 className="text-2xl font-bold text-gray-800 mb-2">Survey Not Available</h2>
          <p className="text-gray-500 max-w-md">
            This survey could not be loaded. It may still be in <strong>Draft</strong> status 
            and needs to be <strong>Published</strong> by your HR before you can respond.
          </p>
        </div>
        <button
          onClick={() => navigate('/surveys')}
          className="flex items-center gap-2 px-6 py-3 bg-indigo-600 hover:bg-indigo-700 rounded-xl text-white font-medium transition-all"
        >
          ← Back to My Surveys
        </button>
      </div>
    );
  }

  if (isSuccess) {
    return (
      <motion.div 
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        className="flex flex-col items-center justify-center min-h-[60vh] space-y-6"
      >
        <div className="w-24 h-24 bg-green-500/20 rounded-full flex items-center justify-center">
          <CheckCircle2 className="w-12 h-12 text-green-400" />
        </div>
        <h2 className="text-3xl font-bold text-gray-800 tracking-tight">Survey Submitted!</h2>
        <p className="text-gray-500 max-w-md text-center">
          Thank you for your feedback. Your responses are being processed by Pulse AI.
        </p>
      </motion.div>
    );
  }

  // If survey loaded but has no questions yet
  if (!survey || questions.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-6 text-center px-4">
        <div className="w-20 h-20 rounded-full bg-blue-500/10 flex items-center justify-center">
          <AlertCircle className="w-10 h-10 text-blue-500" />
        </div>
        <div>
          <h2 className="text-2xl font-bold text-gray-800 mb-2">No Questions Found</h2>
          <p className="text-gray-500 max-w-md">
            This survey has no questions added yet. Please contact your HR.
          </p>
        </div>
        <button
          onClick={() => navigate('/surveys')}
          className="flex items-center gap-2 px-6 py-3 bg-indigo-600 hover:bg-indigo-700 rounded-xl text-white font-medium transition-all"
        >
          ← Back to My Surveys
        </button>
      </div>
    );
  }

  const isCurrentAnswered = currentQuestion 
    ? (getQuestionType(currentQuestion) === 'RATING'
        ? answers[currentQuestion.id] && typeof answers[currentQuestion.id] === 'object' && (answers[currentQuestion.id] as any).rating !== undefined
        : answers[currentQuestion.id] !== undefined && answers[currentQuestion.id] !== '')
    : false;

  const qType = getQuestionType(currentQuestion);
  const qText = getQuestionText(currentQuestion);
  const qOptions = getOptions(currentQuestion);

  const ratingRange = getRatingRange(qText);
  const ratingOptions = Array.from({ length: ratingRange.max - ratingRange.min + 1 }, (_, i) => ratingRange.min + i);
  const useNumberInput = ratingRange.max - ratingRange.min > 20;

  return (
    <div className="max-w-3xl mx-auto py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-800 tracking-tight">{survey?.title || 'Survey'}</h1>
        <p className="text-gray-500 mt-2">{survey?.description}</p>
      </div>

      <div className="flex items-center gap-2 mb-8">
        {questions.map((q, idx) => (
          <div key={q.id ?? idx} className="flex-1 h-2 rounded-full overflow-hidden bg-gray-200">
            <motion.div 
              className={`h-full ${idx <= currentStep ? 'bg-indigo-600' : ''}`}
              initial={{ width: 0 }}
              animate={{ width: idx <= currentStep ? '100%' : '0%' }}
            />
          </div>
        ))}
      </div>

      <AnimatePresence mode="wait">
        <motion.div
          key={currentStep}
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          exit={{ opacity: 0, x: -20 }}
          transition={{ duration: 0.3 }}
        >
          <GlassCard className="min-h-[300px] flex flex-col justify-between">
            <div className="space-y-6">
              <div className="flex items-start justify-between">
                <span className="text-sm font-medium text-indigo-600 bg-indigo-50 px-3 py-1 rounded-full border border-indigo-200">
                  Question {currentStep + 1} of {questions.length}
                </span>
                {qType === 'OPEN_ENDED' && (
                  <Sparkles className="w-5 h-5 text-indigo-400" title="AI Sentiment Analysis will process this answer" />
                )}
              </div>
              
              <h3 className="text-2xl font-semibold text-gray-800 leading-relaxed">
                {qText || <span className="text-gray-400 italic">Question text not available</span>}
              </h3>

              <div className="pt-4">
                {qType === 'RATING' && (
                  <div className="space-y-6">
                    {/* Comment text area box at the top */}
                    <textarea
                      value={(answers[currentQuestion?.id] as any)?.comment || ''}
                      onChange={(e) => handleCommentChange(currentQuestion.id, e.target.value)}
                      placeholder="Type your response here..."
                      className="w-full h-32 p-4 bg-gray-50 border border-gray-200 rounded-xl text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent resize-none"
                    />

                    {/* Rating Selector below that box */}
                    <div>
                      <span className="block text-sm font-semibold text-gray-600 mb-2">
                        Select Rating ({ratingRange.min} to {ratingRange.max}):
                      </span>
                      {useNumberInput ? (
                        <input
                          type="number"
                          min={ratingRange.min}
                          max={ratingRange.max}
                          value={(answers[currentQuestion?.id] as any)?.rating || ''}
                          onChange={(e) => handleRatingChange(currentQuestion.id, Number(e.target.value))}
                          className="w-full p-4 bg-gray-50 border border-gray-200 rounded-xl text-gray-800 focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                          placeholder={`Enter rating between ${ratingRange.min} and ${ratingRange.max}...`}
                        />
                      ) : (
                        <select
                          value={(answers[currentQuestion?.id] as any)?.rating || ''}
                          onChange={(e) => handleRatingChange(currentQuestion.id, Number(e.target.value))}
                          className="w-full p-4 bg-gray-50 border border-gray-200 rounded-xl text-gray-800 focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                        >
                          <option value="" disabled>-- Choose rating ({ratingRange.min} to {ratingRange.max}) --</option>
                          {ratingOptions.map(num => (
                            <option key={num} value={num}>{num}</option>
                          ))}
                        </select>
                      )}
                    </div>
                  </div>
                )}

                {qType === 'OPEN_ENDED' && (
                  <textarea
                    value={answers[currentQuestion?.id] || ''}
                    onChange={(e) => handleAnswer(e.target.value)}
                    placeholder="Type your response here..."
                    className="w-full h-40 p-4 bg-gray-50 border border-gray-200 rounded-xl text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent resize-none"
                  />
                )}

                {qType === 'MULTIPLE_CHOICE' && (
                  <div className="space-y-3">
                    {qOptions.length > 0 ? qOptions.map((opt, i) => (
                      <button
                        key={i}
                        onClick={() => handleAnswer(opt)}
                        className={`w-full text-left p-4 rounded-xl border transition-all ${
                          answers[currentQuestion?.id] === opt
                            ? 'bg-indigo-50 border-indigo-400 text-indigo-800 font-medium'
                            : 'bg-white border-gray-200 text-gray-700 hover:bg-gray-50'
                        }`}
                      >
                        {opt}
                      </button>
                    )) : (
                      <p className="text-gray-400 italic">No options available</p>
                    )}
                  </div>
                )}
              </div>
            </div>

            <div className="flex justify-between items-center mt-12 pt-6 border-t border-gray-100">
              <button
                onClick={handlePrev}
                disabled={currentStep === 0}
                className="flex items-center gap-2 px-4 py-2 text-gray-500 hover:text-gray-800 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
              >
                <ArrowLeft className="w-4 h-4" /> Previous
              </button>

              <button
                onClick={handleNext}
                disabled={!isCurrentAnswered || submitMutation.isPending}
                className="flex items-center gap-2 px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed text-white font-medium rounded-xl transition-all shadow-md"
              >
                {submitMutation.isPending 
                  ? 'Submitting...' 
                  : currentStep === questions.length - 1 
                    ? 'Submit Survey' 
                    : 'Next Question'}
                {!submitMutation.isPending && <ArrowRight className="w-4 h-4" />}
              </button>
            </div>
          </GlassCard>
        </motion.div>
      </AnimatePresence>
    </div>
  );
};